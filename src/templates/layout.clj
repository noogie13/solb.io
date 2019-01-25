(ns templates.layout
  (:require
   [hiccup.core :refer :all]
   [hiccup.element :as elem]
   [hiccup.page :refer [include-css html5]]))

(defn navbar
  []
  (html [:div.navbarcontain
         [:div.navbar
          [:a.left {:href "/"}
           "home"]
          [:a.left {:href "/blog"}
           "blog"]
          [:a.right {:href "#contact"}
           "contact"]]]))


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
     [:header
      (navbar)
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OLOMON"] " BLOCH"]]
     [:div.hobbies
      [:p "hobby typist"]]
     [:div.aboutme
      [:img.face {:src "images/me.jpg"}]
      [:div.social
       (elem/link-to {:target "_blank"} "https://www.linkedin.com/in/solomon-bloch-151309167/"
                     [:img.icons {:src "images/social/256/linkedin.png"}])
       (elem/link-to {:target "_blank"} "https://www.instagram.com/noogietheguru"
                     [:img.icons {:src "images/social/256/insta.png"}])
       (elem/mail-to "mailto:solomonbloch@gmail.com"
                     [:img.icons {:src "images/social/256/google+.png"}])]
      [:br]
      [:br]
      [:br]
      [:div.strengths
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
           [:br]
           [:br] "/home/sol/Documents/"
           [:br]
           [:br] "sol@"
           [:span {:style "color:aqua"}
            "sol-pc"] "-&gt; Love for programming, picks up new languages fast. Special place for functional programming."]]]
        [:div.abouticon
               {:style "background-image:url('images/backgrounds/mars2.jpg'); font-family: 'Computer Modern', serif;"}
         [:div.darken
          [:div.break]
          [:h2 {:style "font-family:'Computer Modern', serif;"}
           "MATHEMATICS"]
          [:div.math-text
           "Studying applied mathematics, enjoys diving into questions of not just how do we solve a problem, but how can we solve it " [:i "optimally."]]]]
        [:div.abouticon
               {:style "background-image:url('images/backgrounds/mars3.jpg')"}
         [:div.darken
          [:div.break]
          [:h2 "DATA ANALYSIS"]
          [:div.math-text
                 {:style "font-family: Alcubierre;"}
           " "
           [:table
            [:tr [:th "Last Name"] [:th "First Name"] [:th "Zip"]]
            [:tr [:th "Hathaway"] [:th "Donny"] [:th "60007"]]
            [:tr [:th "Gaye"] [:th "Marvin"] [:th "20001"]]]
           [:br]
           "But what does it mean? Analyzing data and searching for actionables is a proficiency and interest."]]]]]
      [:div.experience
       [:h2 "EXPERIENCE"]
       [:div.experienceflex
        [:img {:src "images/asseen/fh.png"}]]]
      [:div.asseen
       [:div.colorlayer
        [:div.break]
        [:h2 "AS SEEN"]
        (elem/link-to {:target "_blank"}
                      "http://dailyorange.com/2016/11/super-smash-bros-builds-community-across-central-new-york/")]]]]]))
