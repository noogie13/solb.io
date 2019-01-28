(ns templates.admin
  (:require
   [clojure.java.io :as io]
   [ring.util.anti-forgery :as anti]
   [hiccup.form :as form]
   [hiccup.core :refer :all]
   [hiccup.page :refer [include-css html5]]))

