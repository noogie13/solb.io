(ns backend.blog
  (:require [clj-time.coerce :as tc]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-rss.core :as rss]
            [clojure.core :refer :all]
            [clojure.java.jdbc :as jdbc]
            [hiccup.element :as elem]
            [hiccup.page :refer [html5 include-css include-js]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers :refer :all]
            [cemerick.url :refer [url-encode]]
            [backend.db :as db]
            [ring.util.response :as resp]
            [clojure.string :as str]))

(defn content-format
  [content]
  (-> content
      (str/replace #"(?is)```(.*)```"
                   (str "<pre><code>" "$1" "</pre></code>"))
      (str/replace #"(?is)`(\S*)`"
                   (str "<span class=\"tidbit\">" "$1" "</span>"))
      (str )))

(defn stringify-bytes
  [bytes]
  (->> bytes (map (partial format "%02x")) (apply str)))

(defn edit-post!
  [& {:keys [id title content tags forward]}]
  (jdbc/update! db/pg-db :posts {:forward forward
                                 :tags tags
                                 :title title
                                 :link (url-encode (clojure.string/lower-case
                                                    (clojure.string/replace title " " "-")))
                                 :content content}
                ["id = ?" id])
  (url-encode (clojure.string/lower-case
               (clojure.string/replace title " " "-"))))

(defn edit!
  [req]
  (let* [params (req :params)
         content (:content params)
         title (:title params)
         forward (:forward params)
         tags (:tags params)
         id (:id params)]
    (as-> (edit-post! :content content :title title :forward forward :tags tags :id (read-string id)) $
      (resp/redirect (str "/blog/" $ "/edit")))))

(defn make-draft!
  "make a new post, set as a draft"
  [& {:keys [title tags content forward]
      :or {title "no title"
           tags "no tags"
           content "empty"
           forward "no forward"}}]
  (let [link (clojure.string/lower-case
              (clojure.string/replace title " " "-"))
        date (tc/to-sql-time (t/now))
        content-formatted (content-format content)]
    (jdbc/execute! db/pg-db (-> (insert-into :posts)
                                (columns :title :status
                                         :link :date
                                         :tags :content
                                         :forward)
                                (values
                                 [[title false
                                   link date
                                   tags content-formatted forward]])
                                sql/format))))

(defn drafts-posts
  "pull all draft posts in a sequence"
  []
  (sort-by :date (jdbc/query db/pg-db (-> (select :*)
                                          (from :posts)
                                          (where [:is :status false])
                                          sql/format))))

(defn all-posts
  "duh?"
  []
  (sort-by :date (jdbc/query db/pg-db (-> (select :*)
                                          (from :posts)
                                          sql/format))))

(defn live-posts
  "pull all live posts in a sequence"
  []
  (sort-by :date (jdbc/query db/pg-db (-> (select :*)
                                          (from :posts)
                                          (where [:is :status true])
                                          sql/format))))

(defn like-tag
  "pull other posts with the tag ~tag"
  [tag]
  (sort-by :date (jdbc/query db/pg-db (-> (select :*)
                                          (from :posts)
                                          (where ["tags::text like "
                                                  (str "%" tag "%")])
                                          sql/format))))

(defn generate-rss
  []
  (let [raw-query (sort-by :date (jdbc/query db/pg-db (-> (select :title :link :forward :date :tags)
                                                          (from :posts)
                                                          (where [:is :status true])
                                                          sql/format)))]
    (spit "resources/public/feed.xml"
          (rss/channel-xml {:title "Sol Explores The World" :link "https://solb.io"
                            :description "Mostly posts about programming stuff, maybe something else thrown in once in a while (nothing boring I promise)."}
                           (for [post raw-query]
                             {:title (:title post)
                              :pubDate (:date post)
                              :description (:forward post)
                              :link (str "https://solb.io/blog/" (:link post))
                              :category [(for [tag (str/split (:tags post) #" ")]
                                           [{:domain (str "https://solb.io/blog/tags/" tag)} tag])]})))))

(defn enliven
  [req]
  (let* [id (:id (:params req))
         post (first (jdbc/query db/pg-db
                                 (-> (select :*) (from :posts)
                                     (where [:= :id (Integer/parseInt id)]) sql/format)))
         status (:status post)]
    (jdbc/update! db/pg-db
                  :posts {:status (boolean (not status))
                          :date (tc/to-sql-time (t/now))}
                  ["id = ?" (Integer/parseInt id)]))
  (generate-rss)
  (resp/redirect "/admin"))
