(ns templates.blog
  (:require
   [hiccup.core :refer :all]
   [hiccup.element :as elem]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti]
   [clojure.string :as str]
   [clj-time.format :as f]
   [clj-time.coerce :as tc]
   [backend.db :as db]
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers :refer :all]
   [clojure.java.jdbc :as jdbc]
   [hiccup.page :refer [include-css include-js html5]]
   [backend.blog :as blog]
   [templates.layout :as layout]))

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
      (layout/navbar)]
     [:div.blogonly
      (for [i (reverse post-list)]
        [:div.entry
         [:a.entry {:href (str "/blog/" (:link i))}
          (:title i)]
         [:div.datetagsflex
          [:div.tags
           (for [tag (str/split (:tags i) #" ")]
             (elem/link-to (str "/blog/tags/" tag)
                           (str ":" tag)))]
          [:div.date (f/unparse (f/formatters :date)
                                (tc/from-sql-time (:date i)))]]
         [:p.forward (:forward i)]])]]]))


(defn blog-homepage
  []
  (blog-post-html (blog/live-posts)))

(defn tag-page
  "page that shows posts with the same tag"
  [tag]
  (blog-post-html (blog/like-tag tag)))

(defn admin
  []
  (html5
   (include-css "/styles/style.css")
   [:html
    [:head
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:title "solB"]]
    [:body
     [:header
      (layout/navbar)]
     [:div.blogonly
      (for [i (reverse (blog/live-posts))]
        [:div.entry
         [:a.entry {:href (str "/blog/" (:link i))}
          (:title i)]
         [:div.datetagsflex
          [:div.tags
           (elem/link-to (str "/blog/" (:link i) "/edit")
                         "::edit::")
           (for [tag (str/split (:tags i) #" ")]
             (elem/link-to (str "/blog/tags/" tag)
                           (str ":" tag)))]
          [:div.date (f/unparse (f/formatters :date)
                                (tc/from-sql-time (:date i)))]]
         [:p.forward (:forward i)]])]]]))

(defn htmlitize
  "make a post html (fill up title content etc)"
  [entry-title]
  (let [entry (first (jdbc/query db/pg-db (-> (select :*)
                                              (from :posts)
                                              (where [:= :link
                                                      entry-title])
                                              sql/format)))]
    (html5
     (include-css "/styles/style.css")
     [:html
      [:head
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1.0"}]
       [:title "solB"]]
      [:body
       [:header (layout/navbar)]
       [:div.blog
        [:h2 (:title entry)]
        [:div.datetagsflex
         [:div.tags
          (for [tag (str/split (:tags entry) #" ")]
            (elem/link-to (str "/blog/tags/" tag)
                          (str ":" tag)))]
         [:div.date (f/unparse (f/formatters :date)
                               (tc/from-sql-time (:date entry)))]]
        [:div.content (:content entry)]]]])))

(defn htmlitize-edit!
  "make a post html (fill up title content etc)"
  [entry-title]
  (let [entry (first (jdbc/query db/pg-db (-> (select :*)
                                              (from :posts)
                                              (where [:= :link
                                                      entry-title])
                                              sql/format)))]
    (html5
     (include-css "/styles/style.css")
     (include-js "/scripts/contenteditable.js")
     [:html
      [:head
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1.0"}]
       [:title "solB"]]
      [:body
       [:header (layout/navbar)]
       [:div.blog
        [:h2#sad {:contenteditable "true"} (:title entry)]
        [:div.datetagsflex
         [:div#depressed.tags {:contenteditable "true"}
          (:tags entry)]
         [:div.date (f/unparse (f/formatters :date)
                               (tc/from-sql-time (:date entry)))]]
        [:div#rip.forward {:contenteditable "true"}
         (:forward entry)]
        [:div#unhappy.content {:contenteditable "true"}
         (:content entry)]]
       [:form {:enctype "multipart/form-data"
               :action "/editor"
               :method "post"
               :onsubmit "return getContentEditableAll()"}
        (anti/anti-forgery-field)
        (form/hidden-field "content")
        (form/hidden-field "tags")
        (form/hidden-field "title")
        (form/hidden-field "forward")
        (form/hidden-field "id" (:id entry))
        [:input {:type "submit"}]]]])))
