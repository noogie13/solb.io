(ns templates.layout
  (:require
   [hiccup.core :refer :all]
   [hiccup.element :as elem]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti]
   [clojure.string :as str]
   [clj-time.format :as f]
   [clj-time.coerce :as tc]
   [backend.db :as db]
   [templates.tracking :refer [tracking-head]]
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers :refer :all]
   [clojure.java.jdbc :as jdbc]
   [hiccup.page :refer [include-css include-js html5]]
   [backend.blog :as blog]))

(def navbar
  [:div.navbarcontain
   [:div.navbar {:style "font-family: 'Jacques Francois';"}
    [:a.right {:href "/blog"} "Blog"]
    [:a.right {:href "/aboutme"} "About Me"]
    [:div.navbarname [:a {:href "/"}
                      "Solomon Bloch"]]]])

(def footer
  [:div.footer
   [:div.footercontents
    [:div "©2018 Sol B"]
    [:div
     (elem/link-to "https://www.linkedin.com/in/solomon-bloch-151309167/"
                   "LinkedIn") " | "
     (elem/link-to "https://www.instagram.com/noogietheguru" "Instagram")  " | "
     (elem/link-to "mailto:solomonbloch@gmail.com" "Email")]]])

(def head
  [:head tracking-head
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title "solB"]])

(defmacro page-template
  [options & forms]
  (if-not (map? options)
    `(page-template {} ~options ~@forms)
    `(html5 (include-css "/styles/style.css")
            (if (contains? ~options :js)
              (map include-js (~options :js)))
            [:html ~head
             [:body
              [:div.main
               [:header ~navbar]
               ~@forms]
              footer]])))

(defn homepage
  []
  (page-template
   {:js ["/scripts/contenteditable.js"]}
   [:div.first
    [:div.face [:img.face {:src "images/me.jpg"}]]
    [:div.bloglist
     (for [i (reverse (blog/live-posts))]
       [:div.entry
        [:a.entry {:href (str "/blog/" (:link i))}
         (:title i)]
        [:div.datetagsflex
         [:div.tags
          (for [tag (str/split (:tags i) #" ")]
            (elem/link-to (str "/blog/tags/" tag)
                          (str ":" tag)))]
         [:p.date (f/unparse (f/formatters :date)
                             (tc/from-sql-time (:date i)))]]
        [:div.forward (:forward i)]])]]))

(defn aboutme
  []
  (page-template
   [:div.aboutme
    [:div.strengthflex
     [:img ]]
    [:div.strengthflex
     [:div.abouticon {:style "background-image:url('images/backgrounds/mars1.jpg')"}
      [:div.darken
       [:div.break]
       [:h2 {:style "font-family: Hack, monospace;"}
        "PROGRAMMING"]
       [:div.terminal
        "sol@"
        [:span {:style "color:aqua"}
         "sol-pc"] "-&gt; pwd"
        [:br] [:br] "/home/sol/Documents/" [:br] [:br] "sol@"
        [:span {:style "color:aqua"}
         "sol-pc"] "-&gt; Love for programming, picks up new languages fast. Special place for functional programming."]]]
     [:div.abouticon
      {:style "background-image:url('images/backgrounds/mars2.jpg'); font-family: 'Computer Modern', serif;"}
      [:div.darken
       [:div.break]
       [:h2 {:style "font-family:'Computer Modern', serif; font-weight: normal;"}
        "MATHEMATICS"]
       [:div.math-text
        "Studying applied mathematics, enjoys diving into questions of not just how do we solve a problem, but how can we solve it " [:i "optimally."]]]]
     [:div.abouticon
      {:style "background-image:url('images/backgrounds/mars3.jpg')"}
      [:div.darken
       [:div.break]
       [:h2 "DATA ANALYSIS"]
       [:div.math-text
        {:style "font-family: Raleway;"}
        " "
        [:table.home
         [:tr.home [:th.home "Last Name"] [:th.home "First Name"] [:th.home "Zip"]]
         [:tr.home [:th.home "Hathaway"] [:th.home "Donny"] [:th.home "60007"]]
         [:tr.home [:th.home "Gaye"] [:th.home "Marvin"] [:th.home "20001"]]]
        [:br]
        "But what does it mean? Analyzing data and searching for actionables is a proficiency and interest."]]]]
    [:div.experience
     [:div.break {:style "background-color: #2f363a;"}]
     [:h2 "WORK EXPERIENCE"]
     [:div.experienceflex
      [:img {:src "images/asseen/fh.png" :style "width: 200px; height: 200px;"}]]]
    [:div.asseen
     [:div.colorlayer
      [:div.break]
      [:h2 "AS SEEN"]
      (elem/link-to {:target "_blank"}
                    "http://dailyorange.com/2016/11/super-smash-bros-builds-community-across-central-new-york/")]]]))
