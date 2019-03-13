(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [solb.users :as users]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [templates.layout :as layout]
            [templates.blog :as blog]
            [templates.login :as login]
            [clojure.java.io :as io]))
(defn print-request
  [request]
  (html5 request))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/aboutme" [] (layout/aboutme))
  (GET "/blog/:entry" [entry] (layout/htmlitize entry))
  (GET "/blog" [] (layout/blog-homepage))
  (GET "/blog/tags/:tag" [tag] (layout/tag-page tag))
  (GET "/login" [] (login/login-page))
  (POST "/login" [] users/login-user)
  (GET "/tester" [] users/tester)
  (GET "/prntreq" [] print-request)
  (GET "/admin" [] )
  (route/not-found "where are we going? "))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-cookies)))

(defonce ^:private server (atom nil))

(defn stop-server []
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000})))
