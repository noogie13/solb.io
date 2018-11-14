(ns solb.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [templates.layout :as layout]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] (layout/homepage))
  (GET "/googlee5d78e21ce009a63.html" [] (route/files "googlee5d78e21ce009a63.html"))
  (route/not-found "404 : hi from sol, where ya going? (text me the answer @666-666-6666)"))

(def app
  (wrap-defaults app-routes site-defaults))
