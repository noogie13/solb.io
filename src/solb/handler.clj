(ns solb.handler
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [clj-time.core :as t]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [templates.layout :as layout]
            [templates.blog :as blog]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routee
  (GET "/" [] (layout/homepage))
  (GET "/blog/:entry" [entry] (blog/htmlitize entry))
  (route/not-found "w r u going ?"))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce server (atom nil))

(defn stop-server []
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000})))
