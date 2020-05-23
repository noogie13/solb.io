(ns templates.layout
  (:require
   [hiccup.core :refer :all]
   [hiccup.element :as elem]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti]
   [clojure.string :as str]
   [clj-time.format :as f]
   [clj-time.coerce :as tc]
   [backend.db :as db]
   [templates.tracking :refer [tracking-head]]
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers :refer :all]
   [clojure.java.jdbc :as jdbc]
   [hiccup.page :refer [include-css include-js html5]]
   [backend.blog :as blog]))

(def navbar
  [:div.navbarcontain
   [:div.navbar {:style "font-family: 'Jacques Francois';"}
    [:a.right {:href "/blog"} "Blog"]
    [:a.right {:href "/bio"} "Bio"]
    [:div.navbarname [:a {:href "/"}
                      "Solomon Bloch"]]]])

(def footer
  [:div.footer
   [:div.footercontents
    [:div "©2020 Sol Bloch"]
    [:div
     (elem/link-to "https://www.linkedin.com/in/solomon-bloch-151309167/"
                   "LinkedIn") " | "
     (elem/link-to "https://www.instagram.com/noogietheguru" "Instagram")  " | "
     (elem/link-to "mailto:solomonbloch@gmail.com" "Email")]]])

(def head
  [:head tracking-head
   [:link {:rel "alternate"
           :type "application/rss+xml"
           :title "Sol Explores The World"
           :href "/feed.xml"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title "solB"]])

(defmacro page-template
  [options & forms]
  (if-not (map? options)
    `(page-template {} ~options ~@forms)
    `(html5 (include-css "/styles/style.css")
            (if (contains? ~options :js)
              (if (string? (~options :js))
                (include-js (~options :js))
                (map include-js (~options :js))))
            (if (contains? ~options :title)
              [:title (~options :title)])
            [:html ~head
             [:body
              [:div.main
               [:header ~navbar]
               ~@forms]
              footer]])))

(defn homepage
  []
  (page-template
   {:js "/scripts/contenteditable.js" :title "solB"}
   [:div.first
    [:div.face [:img.face {:src "images/me.jpg"}]]
    [:div.bloglist
     (for [i (reverse (blog/live-posts))]
       [:div.entry
        [:a.entry {:href (str "/blog/" (:link i))}
         (:title i)]
        [:div.datetagsflex
         [:div.tags
          (for [tag (str/split (:tags i) #" ")]
            (elem/link-to (str "/blog/tags/" tag)
                          (str ":" tag)))]
         [:p.date (f/unparse (f/formatters :date)
                             (tc/from-sql-time (:date i)))]]
        [:div.forwardtimeflex
         [:div.forward (:forward i)]
         [:div.readtime "~"
          (int (Math/ceil (/ (count (clojure.string/split (:content i) #"\s+")) 265)))
          " min read"]]])]]))

(defn bio
  []
  (page-template
   {:title "Bio"}
   [:div.blog
    [:div.content
     "I'm Sol, ex-Google-interviewee. I studied at Syracuse University, receiving a major in applied mathematics, and minors in both computer science, and physics. And had a blast! If you'd like to see, here's my "
     (elem/link-to  "https://docs.google.com/document/d/1Q33ErfDa9UBIAdvirshkTVl9We6zalZAxsgp3X_tH1g/edit" "resume") "."
     ]]))

(defn su-cal
  []
  (page-template
   {:js "/scripts/su-cal.js" :title "su-cal"}
   [:div.blog
    [:h1 "ical your schedule"]
    [:div.content
     [:ul
      [:li "Log into MySlice, click on \"View Class Schedule\""]
      [:li "Click \"View Printer Friendly Version\""]
      [:li "Copy the entire page (Ctrl+A then Ctrl+C)"]
      [:li "Paste into the text field below."]
      [:li "Click the button, save the file as a .ics file (something with .ics after it's name, e.g. "
       [:code.tidbit "cal.ics"] "."]
      "Then you can import this file into any calendar appliation you'd like."
      [:br]
      "NOTE: I " [:strong "STRONGLY"]
      " recommend making a new calendar on Google Calendar before importing the ICS. Just in case something breaks."]]
    (form/text-area {:id "cal-raw"} "cal")
    [:button {:onclick "window.open('/su-cal-gen?cal=' + encodeURI(document.getElementById('cal-raw').value.replace(/&/g,'')))"
              :style "display: block; margin: auto;"}
     "get ics"]]))
