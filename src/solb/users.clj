(ns solb.users
  (:require
   [ring.util.response :refer [response redirect]]
   [clj-time.core :as time]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as sql]
   [honeysql.helpers :refer :all :as helpers]
   [templates.db :refer [pg-db]]
   [hiccup.page :refer [html5]]
   [buddy.sign.jwt :as jwt]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [buddy.auth.backends.token :refer [jws-backend]]
   [buddy.hashers :as hashers]))

(def secret "mysecret")

(def backend (jws-backend {:secret secret :options {:alg :hs512}}))

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
          (let [updated-session (assoc session :identity (:username query))]
            (-> (redirect "/admin")
                (assoc :session updated-session)))
          (html5 "no"))
        (html5 "no")))))
