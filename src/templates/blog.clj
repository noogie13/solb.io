(ns templates.blog
  (:require [clj-time.coerce :as tc]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.core :refer :all]
            [clojure.java.jdbc :as jdbc]
            [hiccup.element :as elem]
            [hiccup.page :refer [html5 include-css]]
            [honeysql.core :as sql]
            [honeysql.helpers :as helpers :refer :all]
            [templates.db :as db]
            [templates.layout :as layout]))

(defn make-draft
  [& {:keys [title tags content]
      :or {title "no title"
           tags "no tags"
           content "empty"}}]
  (let [link (clojure.string/lower-case (clojure.string/replace title " " "-"))
        date (tc/to-sql-time (t/now))]
    (jdbc/execute! db/pg-db (-> (insert-into :posts)
                                (columns :title :status :link :date :tags :content)
                                (values
                                 [[title true link date tags content]])
                                sql/format))))

(defn drafts-posts
  []
  (jdbc/query db/pg-db (-> (select :*)
                           (from :posts)
                           (where [:is :status false])
                           sql/format)))

(defn live-posts
  []
  (jdbc/query db/pg-db (-> (select :*)
                           (from :posts)
                           (where [:is :status true])
                           sql/format)))

(defn htmlitize
  [entry-title]
  (let [entry (first (jdbc/query db/pg-db (-> (select :*)
                                              (from :posts)
                                              (where [:= :link entry-title])
                                              sql/format)))]
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
       [:div {:class "blog"}
        [:h2 (:title entry)]
        [:p {:class "date"} (:date entry)]
        [:p (:content entry)]]]])))

(defn blog-homepage
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
      (layout/navbar)
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OL"] "B"]]
     [:table {:style "width:100%; color:black;"}
      (for [i (live-posts)]
        [:tr
         [:th (elem/link-to (str "blog/" (:link i))
                            (:title i))]
         [:th (:date i)]])]]]))
