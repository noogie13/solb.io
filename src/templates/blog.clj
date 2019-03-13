(ns templates.blog
  (:require [clj-time.coerce :as tc]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.core :refer :all]
            [clojure.java.jdbc :as jdbc]
            [hiccup.element :as elem]
            [hiccup.page :refer [html5 include-css include-js]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers :refer :all]
            [templates.db :as db]
            [glow.core :as glow]
            [clojure.string :as str]))

(defn content-format
  [content]
  (-> content
      (str/replace #"(?is)```(.*)```"
                   (str "<pre><code>" "$1" "</pre></code>"))
      (str/replace #"`(.*)`"
                   (str "<span class=\"tidbit\">" "$1" "</span>"))
      (str )))


(defn make-draft!
  "make a new post, set as a draft"
  [& {:keys [title tags content forward]
      :or {title "no title"
           tags "no tags"
           content "empty"
           forward "no forward"}}]
  (let [link (clojure.string/lower-case
              (clojure.string/replace title " " "-"))
        date (tc/to-sql-time (t/now))]
    (jdbc/execute! db/pg-db (-> (insert-into :posts)
                                (columns :title :status
                                         :link :date
                                         :tags :content
                                         :forward)
                                (values
                                 [[title true
                                   link date
                                   tags content forward]])
                                sql/format))))

(defn drafts-posts
  "pull all draft posts in a sequence"
  []
  (jdbc/query db/pg-db (-> (select :*)
                           (from :posts)
                           (where [:is :status false])
                           sql/format)))

(defn live-posts
  "pull all live posts in a sequence"
  []
  (jdbc/query db/pg-db (-> (select :*)
                           (from :posts)
                           (where [:is :status true])
                           sql/format)))

(defn like-tag
  "pull other posts with the tag ~tag"
  [tag]
  (jdbc/query db/pg-db (-> (select :*)
                           (from :posts)
                           (where ["tags::text like "
                                   (str "%" tag "%")])
                           sql/format)))
