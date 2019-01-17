(ns solb.handler
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [templates.layout :as layout]
            [templates.blog :as blog]
            [templates.post :as post]
            [clojure.java.io :as io]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults api-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/blog/:entry" [entry] (blog/htmlitize entry))
  (GET "/blog" [] (blog/blog-homepage))
  (GET "/test" [] (post/post-page))
  (POST "/file" {params :params}
        (let* [temp (get params :file)]
          (str "size: " (temp :size) "    name: " (temp :filename))))
  (route/not-found "w r u going ?"))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce ^:private server (atom nil))

(defn stop-server []
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000})))
