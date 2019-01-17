(ns templates.post
  (:require
   [clojure.java.io :as io]
   [ring.util.anti-forgery :as anti]
   [hiccup.form :as form]
   [hiccup.core :refer :all]
   [hiccup.page :refer [include-css html5]]))

(defn post-page []
  (html5
   (form/form-to {:enctype "multipart/form-data"}
                 [:post "/file"]
                 (anti/anti-forgery-field)
                 (form/file-upload "file")
                 (form/submit-button "submit?"))))
