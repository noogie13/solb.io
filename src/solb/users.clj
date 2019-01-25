(ns solb.users
  (:require
   [clj-time.core :as time]
   [clojure.java.io :as io]
   [clojure.java.jdbc :as jdbc]
   [ring.util.response :refer [response redirect]]
   [honeysql.core :as sql]
   [honeysql.helpers :refer :all :as helpers]
   [templates.db :refer [pg-db]]
   [hiccup.page :refer [html5]]
   [buddy.core.nonce :as nonce]
   [buddy.sign.jwe :as jwe]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [buddy.auth.backends.token :refer [jwe-backend]]
   [buddy.hashers :as hashers]))

(def secret (nonce/random-bytes 32))

(def backend (jwe-backend {:secret secret :options {:alg :a256kw :enc :a128gcm}}))

(defn create-user!
  "allows duplicates, so be careful here :>"
  [request]
  (let* [params (:params request)
         name (params :name)
         username (params :username)
         password (hashers/derive (params :password))]
    (jdbc/execute! pg-db (-> (insert-into :users)
                             (columns :username :password :name)
                             (values
                              [[username password name]])
                             sql/format))
    (html5 "created")))

(defn login-user
  "assoc user to session"
  [request]
  (let* [params (:params request)
         username (params :username)
         password (params :password)
         session (:session request)]
    (let [query (first (jdbc/query pg-db (-> (select :*)
                                             (from :users)
                                             (where [:= :username username])
                                             sql/format)))]
      (if (not (nil? query))
        (if (hashers/check password (:password query))
          (let [claims {:user (keyword username)
                        :exp (time/plus (time/now) (time/seconds 3600))}
                token (jwe/encrypt claims secret {:alg :a256kw :enc :a128gcm})]
            (html5 (str {:token token})))
          (html5 "no"))
        (html5 "no")))))
