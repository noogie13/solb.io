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
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers :refer :all]
   [clojure.java.jdbc :as jdbc]
   [hiccup.page :refer [include-css include-js html5]]
   [backend.blog :as blog]))

(defn navbar
  []
  (html [:div.navbarcontain
         [:div.navbar {:style "font-family: 'Jacques Francois';"}
          ;; [:a.left {:href "/"} "Home"]
          [:a.right {:href "/blog"} "Blog"]
          [:a.right {:href "/aboutme"} "About Me"]
          [:div.navbarname [:a {:href "/"}
                            "Solomon Bloch"]]]]))


(defn homepage
  []
  (html5
   (include-css "styles/style.css")
   [:html
    [:head
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:title "solB"]]
    [:body
     [:header (navbar)]
     ;; [:h1 "S"
     ;;  [:span {:style "font-size: 26px;"}
     ;;   "OLOMON"] " BLOCH"]
     ;; [:div.hobbies
     ;;  [:p "hobby typist"]]
     [:div.first
      [:div.face
       [:img.face {:src "images/me.jpg"}]
       [:div.social
        (elem/link-to {:target "_blank"} "https://www.linkedin.com/in/solomon-bloch-151309167/"
                      [:img.icons {:src "images/social/256/linkedin.png"}])
        (elem/link-to {:target "_blank"} "https://www.instagram.com/noogietheguru"
                      [:img.icons {:src "images/social/256/insta.png"}])
        (elem/mail-to "mailto:solomonbloch@gmail.com"
                      [:img.icons {:src "images/social/256/google+.png"}])]]
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
          [:p.forward (:forward i)]])
       ;; [:div.more
       ;;  [:a {:href "/blog"}
       ;;   [:div.morewords "↓ see more posts ↓"]]]
       ]]]]))

(defn aboutme
  []
  (html5
   (include-css "styles/style.css")
   [:html
    [:head
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:title "solB"]]
    [:body
     [:header (navbar)]
     [:div.aboutme
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
       [:h2 "EXPERIENCE"]
       [:div.experienceflex
        [:img {:src "images/asseen/fh.png" :style "width: 200px; height: 200px;"}]]]
      [:div.asseen
       [:div.colorlayer
        [:div.break]
        [:h2 "AS SEEN"]
        (elem/link-to {:target "_blank"}
                      "http://dailyorange.com/2016/11/super-smash-bros-builds-community-across-central-new-york/")]]]]]))
