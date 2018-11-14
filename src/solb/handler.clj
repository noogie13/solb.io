(ns solb.handler
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [clj-time.core :as t]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [templates.layout :as layout]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/test" [] (str (t/now)))
  (route/files "/static/")
  (route/not-found "404 : hi from sol, where ya going? (text me the answer @666-666-6666)"))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce server (atom nil))

(defn stop-server []
  (@server :timeout 5)
  (reset! server nil))

(defn -main [& args]
  (reset! server (run-server #'app {:port 3000})))
