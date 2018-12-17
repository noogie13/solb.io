(ns templates.blog



  (:require
   [clojure.core :refer :all]
   [clojure.java.io :as io]
   [clj-time.format :as f]
   [clj-time.core :as t]
   [clj-time.local :as l]
   [hiccup.core :refer :all]
   [hiccup.element :refer [image]]
   [hiccup.page :refer [html5 include-css]]))

(def time-date-format (f/formatters :basic-date-time))
(def custom-formatter (f/formatter "h:ma  MM-dd-yyyy"))
;; so I don't have to type (f/formatters :basic-date) every time

(defn open-entry-file
  [entry]
  (with-open [rdr (io/reader entry)]
    (let [data {}
          seq (line-seq rdr)]
      (assoc data
             :title (nth seq 0)
             :date (nth seq 1)
             :tags (nth seq 2)
             :content (nth seq 3)))))

(defn open-entry
  [entry]
  (let [filename (str "resources/public/blog/live/" entry)]
    (open-entry-file (io/file filename))))

(defn entry-save-draft
  "saves to drafts"
  [& {:keys [title tags content]
      :or {title "no title" tags "no tags" content "empty"}}]
  (let* [titlefix (clojure.string/lower-case (clojure.string/replace title " " "-"))
         filestring (str "resources/public/blog/drafts/" titlefix)
         file-contents (list title
                             (f/unparse time-date-format (l/local-now))
                             tags
                             content)]
    (if (.exists (io/file filestring))
      nil
      (with-open [wrtr (io/writer filestring)]
        (doseq [i file-contents]
          (.write wrtr (str i "\n")))))))

(defn list-drafts
  "returns a list of files (use str on each to cast them to string)"
  []
  (.listFiles (io/file "resources/public/blog/drafts/")))

(defn list-live
  "returns a list of files live"
  []
  (.listFiles (io/file "resources/public/blog/live/")))

(defn print-list
  "just to print the live or drafts"
  [something]
  (doseq [i something]
    (println i)))

(defn make-live
  "give a draft and makes it live (renames it to be in the live folder)"
  [entry]
  (let [oldname (str entry)
        newname (clojure.string/replace oldname "drafts" "live")]
    (.renameTo entry (io/file newname))))


(defn htmlitize
  [entry-title]
  (let [entry (open-entry entry-title)]
    (html5
     (include-css "/styles/style.css")
     [:html
      [:head
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1.0"}]
       [:title "solB"]]
      [:body
       [:header
        [:div {:class "navbarcontain"}
         [:div {:class "navbar"}
          [:a {:href "/"
               :class "left"}
           "home"]
          [:a {:href "#contact"
               :class "right"}
           "contact"]]]
        [:h1 "S"
         [:span {:style "font-size: 26px;"}
          "OL"] "B"]]
       [:div {:class "blog"}
        [:h2 (entry :title)]
        [:p {:class "date"} (f/unparse custom-formatter
                                       (f/parse time-date-format (entry :date)))]
        [:p (load-string (entry :content))]]]])))

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
      [:div {:class "navbarcontain"}
       [:div {:class "navbar"}
        [:a {:href "/"
             :class "left"}
         "home"]
        [:a {:href "#contact"
             :class "right"}
         "contact"]]]
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OL"] "B"]]
     [:table {:style "width:100%; color:black;"}
      (doseq [entry-closed (list-live)]
        (let [entry (open-entry-file entry-closed)]
          [:tr [:th (entry :title)]]))]]]))
