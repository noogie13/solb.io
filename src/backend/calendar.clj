(ns backend.calendar
  (:require
   [clojure.java.shell :refer [sh]]
   [clojure.java.io :as io]
   [ring.util.codec :as codec]))

(defonce path "src/backend/")

(defn get-ics-string
  [input-string]
  (->> (sh "./calscript.py" input-string :dir path)
       (:out)))

(defn get-ics-from-req
  [req]
  (let [string (->> (req :params)
                    (reduce into [])
                    (apply str)
                    (get-ics-string))]
    {:status 200
     :headers {}
     :body string}))
