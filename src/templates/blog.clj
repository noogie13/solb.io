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
            [templates.layout :as layout]
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

(defn blog-post-html
  "takes a list of blog entries and turns it into html"
  [post-list]
  (html5
   (include-css "/styles/style.css")
   [:html
    [:head
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:title "solB"]]
    [:body
     [:header
      (layout/navbar)
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OL"] "B"]]
     [:div.bloglist
      (for [i (reverse post-list)]
        [:div.entry
         [:a.entry {:href (str "/blog/" (:link i))}
          (:title i)]
         [:div.tags
          (for [tag (str/split (:tags i) #" ")]
            (elem/link-to (str "/blog/tags/" tag)
                          (str ":" tag)))]
         [:p.forward (:forward i)]
         [:p.date (f/unparse (f/formatters :date)
                             (tc/from-sql-time (:date i)))]])]]]))


(defn blog-homepage
  []
  (blog-post-html (live-posts)))

(defn tag-page
  "page that shows posts with the same tag"
  [tag]
  (blog-post-html (like-tag tag)))



(defn htmlitize
  "make a post html (fill up title content etc)"
  [entry-title]
  (let [entry (first (jdbc/query db/pg-db (-> (select :*)
                                              (from :posts)
                                              (where [:= :link
                                                      entry-title])
                                              sql/format)))]
    (html5
     (include-css "/styles/style.css" )
     [:html
      [:head
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1.0"}]
       [:title "solB"]]
      [:body
       [:header
        (layout/navbar)
        [:h1 "S"
         [:span {:style "font-size: 26px;"}
          "OL"] "B"]]
       [:div.blog
        [:h2 (:title entry)]
        [:div.tags
         (for [tag (str/split (:tags entry) #" ")]
           (elem/link-to (str "/blog/tags/" tag)
                         (str ":" tag)))]
        [:p.date (f/unparse (f/formatters :date)
                            (tc/from-sql-time (:date entry)))]
        [:p (:content entry)]]]])))

(defn htmltest
  "make a post html (fill up title content etc)"
  []
  (let [entry {:title "clojure blog 1: hiccup and ring",
               :tags "these are tags",
               :date (tc/to-sql-time (t/now)),
               :content (content-format "Clojure blogging; a  bb")}]
    (html5
     (include-css "/styles/style.css")
     [:html
      [:head
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1.0"}]
       [:title "solB"]]
      [:body
       [:header
        (layout/navbar)
        [:h1 "S"
         [:span {:style "font-size: 26px;"}
          "OL"] "B"]]
       [:div.blog
        [:h2 (:title entry)]
        [:div.tags
         (for [tag (str/split (:tags entry) #" ")]
           (elem/link-to (str "/blog/tags/" tag)
                         (str ":" tag)))]
        [:p.date (f/unparse (f/formatters :date)
                            (tc/from-sql-time (:date entry)))]
        [:div.content (:content entry)]]]])))
