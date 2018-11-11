(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [templates.layout :as layout]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
