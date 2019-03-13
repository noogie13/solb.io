(ns templates.db
  (:require
   [honeysql.core :as sql]
   [honeysql.helpers :refer :all :as helpers]
   [clojure.java.jdbc :as jdbc]))

(def pg-db {:dbtype "postgresql"
            :dbname "blog"
            :host "localhost"
            :port "5432"
            :user "blogger"
            :password "password"})


;; (def posts (jdbc/create-table-ddl :posts [[:title "VARCHAR(255)"]
;;                                           [:status "bool"]
;;                                           [:link "VARCHAR(255)"]
;;                                           [:date "TIMESTAMP"]
;;                                           [:tags "VARCHAR(255)"]
;;                                           [:forward "VARCHAR(255)"]
;;                                           [:content "TEXT"]]))
;; (jdbc/execute! pg-db posts)
;; (def users (jdbc/create-table-ddl :users [[:username "VARCHAR(255)"]
;;                                           [:password "VARCHAR(255)"]
;;                                           [:name "VARCHAR(255)"]]))
