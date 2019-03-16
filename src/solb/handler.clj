(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [backend.users :as users]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [templates.layout :as layout]
            [templates.login :as login]
            [templates.blog :as blog]
            [clojure.java.io :as io]))

(defn print-request
  [request]
  (html5 request))

(defn print-content
  [req]
  (html5  (req :params)))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/aboutme" [] (layout/aboutme))
  (GET "/blog/:entry" [entry :as req] (blog/htmlitize entry))
  (GET "/blog/:entry/edit" [entry :as req]
       (users/sol? req (blog/htmlitize-edit! entry)))
  (POST "/editor" [:as req] (users/sol? req (backend.blog/edit! req)))
  (POST "/enlive" [:as req] (users/sol? req (backend.blog/enliven req)))
  (GET "/newpost" [:as req] (users/sol? req (blog/new-post)))
  (GET "/blog" [] (blog/blog-homepage))
  (GET "/blog/tags/:tag" [tag] (blog/tag-page tag))
  (GET "/login" [] (login/login-page))
  (POST "/login" [] users/login-user)
  ;; (GET "/tester" [] users/tester)
  ;; (GET "/prntreq" [] print-request)
  (GET "/admin" [:as req] (users/sol? req (blog/admin req)))
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
