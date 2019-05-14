(ns backend.utils
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers :refer :all]
            [backend.db :refer [pg-db]]
            [ring.util.response :as resp]))

(defonce characters "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

(def file-storage-location "/home/sol/files/")

(defn random-chars
  [n]
  (->> (repeatedly #(rand-nth characters))
       (take n)
       (reduce str)))

(defn generate-token
  []
  (random-chars 30))

(defn unique-id
  []
  (let [new-id (random-chars (+ 4 (rand-int 4)))]
    (if (first (jdbc/query pg-db (-> (select :id)
                                     (from :shortened)
                                     (where [:= new-id :id])
                                     sql/format)))
      (unique-id)
      new-id)))

(defn validate-token-return-username
  [token]
  (:username (first (jdbc/query pg-db (-> (select :username)
                                          (from :users)
                                          (where [:= :token token])
                                          sql/format)))))

(defn file-upload
  [req]
  (let* [id (unique-id)
         token (get (req :params) "token")
         data (str file-storage-location id)]
    (when-let [username (validate-token-return-username token)]
      (cond
        (string? (get (req :params) "file")) (spit data (get (req :params) "file"))
        :else (when (< (:size (get (req :params) "file")) 20000000)
                (io/copy (:tempfile (get (req :params) "file"))
                         (io/file data))))
      (jdbc/execute!
       pg-db
       (-> (insert-into :shortened)
           (columns :id :type :data :username)
           (values
            [[id (get (req :params) "type") "" username]])
           sql/format))
      (str "https://solb.io/" id))))

(defn redirect-upload
  [req]
  (let* [id (unique-id)
         token (get (req :params) "token")
         data (get (req :params) "redirect")]
    (let [username (validate-token-return-username token)]
      (when username
        (jdbc/execute! pg-db (-> (insert-into :shortened)
                                 (columns :id :type :data :username)
                                 (values
                                  [[id "redirect" data username]])
                                 sql/format))
        (str "https://solb.io/" id)))))

(defn return-shortened
  [id]
  (let [query-result (first (jdbc/query pg-db (-> (select :type :data :id)
                                                  (from :shortened)
                                                  (where [:= :id id])
                                                  sql/format)))]
    (if (= (query-result :type) "redirect")
      {:status 302
       :headers {"Location" (query-result :data)}
       :body ""}
      {:status 200
       :headers {"Content-Type" (query-result :type)}
       :body (io/file (str file-storage-location (query-result :id)))})))
