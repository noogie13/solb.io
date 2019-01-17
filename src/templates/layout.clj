(ns templates.layout
  (:require
   [hiccup.core :refer :all]
   [hiccup.page :refer [include-css html5]]))

(defn navbar []
  (html [:div {:class "navbarcontain"}
         [:div {:class "navbar"}
          [:a {:href "/"
               :class "left"}
           "home"]
          [:a {:href "/blog"
               :class "left"}
           "blog"]
          [:a {:href "#contact"
               :class "right"}
           "contact"]]]))


(defn homepage []
  (html5
   (include-css "styles/style.css" "hack-font/hack.css")
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
     ;; [:div {:class "hobbies"}
     ;;  [:p "hobby typist"]]
     [:div {:class "aboutme"}
      [:img {:src "images/me.jpg", :class "face"}]
      [:div {:class "social"}
       [:a {:href "https://www.linkedin.com/in/solomon-bloch-151309167/"
            :class "social"
            :target "_blank"}
        [:img {:src "images/social/256/linkedin.png"
               :class "icons"}]]
       [:a {:href "https://www.instagram.com/noogietheguru"
            :target "_blank"}
        [:img {:src "images/social/256/insta.png"
               :class "icons"}]]
       [:a {:href "mailto:solomonbloch@gmail.com"}
        [:img {:src "images/social/256/google+.png"
               :class "icons"}]]]
      [:br]
      [:br]
      [:br]
      [:div {:class "strengths"}
       [:div {:class "strengthflex"}
        [:div {:class "abouticon"
               :style "background-image:url('images/backgrounds/mars1.jpg')"}
         [:div {:class "darken"}
          [:div {:class "break"}]
          [:h2 {:style "font-family: Hack, monospace;"}
           "PROGRAMMING"]
          [:div {:class "terminal"}
           "sol@"
           [:span {:style "color:aqua"}
            "sol-pc"] "-&gt; pwd"
           [:br]
           [:br] "/home/sol/Documents/"
           [:br]
           [:br] "sol@"
           [:span {:style "color:aqua"}
            "sol-pc"] "-&gt; Love for programming, picks up new languages fast. Special place for functional programming."]]]
        [:div {:class "abouticon"
               :style "background-image:url('images/backgrounds/mars2.jpg'); font-family: 'Computer Modern', serif;"}
         [:div {:class "darken"}
          [:div {:class "break"}]
          [:h2 {:style "font-family:'Computer Modern', serif;"}
           "MATHEMATICS"]
          [:div {:class "math-text"}
           "Studying applied mathematics, enjoys diving into questions of not just how do we solve a problem, but how can we solve it " [:i "optimally."]]]]
        [:div {:class "abouticon"
               :style "background-image:url('images/backgrounds/mars3.jpg')"}
         [:div {:class "darken"}
          [:div {:class "break"}]
          [:h2 "DATA ANALYSIS"]
          [:div {:class "math-text"
                 :style "font-family: Alcubierre;"}
           " "
           [:table
            [:tr
             [:th "Last Name"]
             [:th "First Name"]
             [:th "Zip"]]
            [:tr
             [:th "Hathaway"]
             [:th "Donny"]
             [:th "60007"]]
            [:tr
             [:th "Gaye"]
             [:th "Marvin"]
             [:th "20001"]]]
           [:br]
           "But what does it mean? Analyzing data and searching for actionables is a proficiency and interest."]]]]]
      [:div {:class "experience"}
       [:h2 "EXPERIENCE"]
       [:div {:class "experienceflex"}
        [:img {:src "images/asseen/fh.png"}]]]
      [:div {:class "asseen"}
       [:div {:class "colorlayer"}
        [:div {:class "break"}]
        [:h2 "AS SEEN"]
        [:a {:href "http://dailyorange.com/2016/11/super-smash-bros-builds-community-across-central-new-york/"
             :target "_blank"}]]]]]]))
