(ns backend.users
  (:require
   [clj-time.core :as t]
   [clojure.java.io :as io]
   [clojure.java.jdbc :as jdbc]
   [ring.util.response :refer [response redirect]]
   [honeysql.core :as sql]
   [honeysql.helpers :refer :all :as helpers]
   [backend.db :refer [pg-db]]
   [hiccup.page :refer [html5]]
   [cheshire.core :as json]
   [buddy.core.nonce :as nonce]
   [buddy.core.keys :as keys]
   [buddy.sign.jwt :as jwt]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [buddy.auth.backends.token :refer [jws-backend]]
   [buddy.hashers :as hashers]))

;; "es256"

(def privkey (keys/private-key "src/backend/keys/ecprivkey.pem"))
(def pubkey (keys/public-key "src/backend/keys/ecpubkey.pem"))

;; (def backend (jws-backend {:secret privkey
;;                            :options {:alg :es256}}))

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
         username (:username params)
         password (:password params)
         session (:session request)
         query (first (jdbc/query pg-db (-> (select :*)
                                            (from :users)
                                            (where [:= :username username])
                                            sql/format)))]
    (if (not (nil? query))
      (if (hashers/check password (:password query))
        (let [claims {:user (keyword username)}
              token (jwt/sign claims privkey {:alg :es256})]
          (-> (redirect "/")
              ;; (assoc :session {:token token})
              (assoc :cookies {"token" {:value token, :max-age 259200}})))
        "no")
      "no")))

(defmacro sol?
  [request next]
  `(if (= "sol" ((jwt/unsign (((~request :cookies) "token") :value)
                            pubkey {:alg :es256}) :user))
    ~next
    (html5 "not authorized --")))

(defn tester
  [request]
  (html5 "hi"))
;; (html5 (jwt/unsign (((request :cookies) "token") :value)
;;                    pubkey {:alg :es256})))
