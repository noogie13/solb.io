(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [backend.users :as users]
            [backend.utils :as utils]
            [backend.calendar :as cal]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :as resp]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [templates.layout :as layout]
            [templates.login :as login]
            [templates.blog :as blog]
            [clojure.java.io :as io]))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/aboutme" [] (layout/aboutme))
  (context "/blog" []
           (GET "/" [] (blog/blog-homepage))
           (GET "/tags/:tag" [tag] (blog/tag-page tag))
           (GET "/:entry" [entry :as req] (blog/htmlitize entry))
           (GET "/:entry/edit" [entry :as req]
                (users/sol? req (blog/htmlitize-edit! entry))))
  (GET "/su-cal" [] (layout/su-cal))
  (GET "/su-cal-gen" [:as req] (cal/get-ics-from-req req))
  (POST "/editor" [:as req] (users/sol? req (backend.blog/edit! req)))
  (POST "/enlive" [:as req] (users/sol? req (backend.blog/enliven req)))
  (POST "/login" [] users/login-user)
  (GET "/newpost" [:as req] (users/sol? req (blog/new-post)))
  (GET "/login" [] (login/login-page))
  (GET "/admin" [:as req] (users/sol? req (blog/admin req)))
  (GET "/showtoken" [:as req] (users/show-token req))
  (GET "/generatetoken" [:as req] (users/add-token req))
  (GET "/:id{[a-zA-Z0-9]{4,8}}" [id] (utils/return-shortened id))
  (route/not-found "where are we going? "))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-cookies)))

(defroutes backend-routes
  (POST "/fileupload" [:as req] (utils/file-upload req))
  (POST "/shorten" [:as req] (utils/redirect-upload req)))

(def backend-app
  (-> backend-routes
      (wrap-params)
      (wrap-multipart-params)))

(defonce ^:private server (atom nil))
(defonce ^:private backend-server (atom nil))

(defn stop-servers [server]
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000}))
  (reset! backend-server (run-server #'backend-app {:port 4000})))
