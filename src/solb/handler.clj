(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [solb.users :as users]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [templates.layout :as layout]
            [templates.blog :as blog]
            [templates.login :as login]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [clojure.java.io :as io]))

(defroutes app-routes
  ;; (GET "/" [] (layout/homepage))
  (GET "/" [] (layout/homepage))
  (GET "/blog/:entry" [entry] (blog/htmlitize entry))
  (GET "/blog" [] (blog/blog-homepage))
  (GET "/blog/tags/:tag" [tag] (blog/tag-page tag))
  (GET "/login" [] (login/login-page))
  (POST "/login" [] users/login-user)
  (GET "/create" [] (login/create-page))
  (POST "/create" [] users/create-user!)
  (GET "/admin" [] (html5 "hi"))
  (GET "/htmltest" [] (blog/htmltest))
  (route/not-found "w r u going ?"))

(def app
  (as-> app-routes $
    (wrap-defaults $ site-defaults)))

(defonce ^:private server (atom nil))

(defn stop-server []
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000})))
