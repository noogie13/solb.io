(ns templates.login
  (:require
   [clojure.java.io :as io]
   [ring.util.anti-forgery :as anti]
   [hiccup.form :as form]
   [hiccup.core :refer :all]
   [hiccup.page :refer [include-css html5]]))

(defn create-page
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
      (templates.layout/navbar)
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OL"] "B"]]
     [:h1 {:style "color: black;"} "create"]
     [:div#create.form
      (form/form-to {:enctype "multipart/form-data"}
                    [:post "/create"]
                    (anti/anti-forgery-field)
                    "username"
                    (form/text-field "username")
                    [:br]
                    "name"
                    (form/text-field "name")
                    [:br]
                    "password"
                    (form/password-field "password")
                    (form/submit-button "submit?"))]]]))

(defn login-page
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
      (templates.layout/navbar)
      [:h1 "S"
       [:span {:style "font-size: 26px;"}
        "OL"] "B"]]
     [:h1 {:style "color:black;"} "log"]
     [:div#login.form
      (form/form-to {:enctype "multipart/form-data"}
                    [:post "/login"]
                    (anti/anti-forgery-field)
                    "user"
                    (form/text-field "username")
                    [:br]
                    "pass"
                    (form/password-field "password")
                    (form/submit-button "submit?"))]]]))

(defn post-page []
  (html5
   (form/form-to {:enctype "multipart/form-data"}
                 [:post "/file"]
                 (anti/anti-forgery-field)
                 (form/file-upload "file")
                 (form/submit-button "submit?"))))
