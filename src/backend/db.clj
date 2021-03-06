(ns backend.db
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


;; (def posts (jdbc/create-table-ddl :posts [[:id "int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY"]
;;                                           [:title "VARCHAR(255)"]
;;                                           [:status "bool"]
;;                                           [:link "VARCHAR(255)"]
;;                                           [:date "TIMESTAMP"]
;;                                           [:tags "VARCHAR(255)"]
;;                                           [:forward "VARCHAR(255)"]
;;                                           [:content "TEXT"]]))
;; (jdbc/execute! pg-db shortened)

;; (defn post []
;;   (jdbc/execute! pg-db posts))

;; (def users (jdbc/create-table-ddl :users [[:username "VARCHAR(255)"]
;;                                           [:password "VARCHAR(255)"]
;;                                           [:name "VARCHAR(255)"]
;;                                           [:token "VARCHAR(255)"]]))

;; (def shortened (jdbc/create-table-ddl :shortened [[:id "VARCHAR(255)"]
;;                                                   [:username "VARCHAR(255)"]
;;                                                   [:type "VARCHAR(255)"]
;;                                                   [:data "VARCHAR(255)"]]))
