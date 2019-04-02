(ns templates.login
  (:require
   [clojure.java.io :as io]
   [ring.util.anti-forgery :as anti]
   [templates.layout :as layout]
   [hiccup.form :as form]
   [hiccup.core :refer :all]
   [hiccup.page :refer [include-css html5]]))

(defn create-page
  []
  (layout/page-template
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
                  (form/submit-button "submit?"))]))

(defn login-page
  []
  (layout/page-template
   [:div#login.form
    (form/form-to {:enctype "multipart/form-data"}
                  [:post "/login"]
                  (anti/anti-forgery-field)
                  "username"
                  (form/text-field "username")
                  [:br]
                  "password"
                  (form/password-field "password")
                  (form/submit-button "submit?"))]))
