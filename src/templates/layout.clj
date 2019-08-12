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
    [:div "©2018 Sol B"]
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
            [:html ~head
             [:body
              [:div.main
               [:header ~navbar]
               ~@forms]
              footer]])))

(defn homepage
  []
  (page-template
   {:js "/scripts/contenteditable.js"}
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
        [:div.forward (:forward i)]])]]))

(defn bio
  []
  (page-template
   [:div.bio
    [:div.info "Hi! I'm Sol and I like to learn. I study at Syracuse University working towards a B.S. in Applied Mathematics, and minors in both Physics and Computer Science. I like to work hard, and take great pride in success. My (ugly) " (elem/link-to {:class navbar} "https://docs.google.com/document/d/1Q33ErfDa9UBIAdvirshkTVl9We6zalZAxsgp3X_tH1g/edit" "resume") "."]
    [:div.strengthflex
     [:div.abouticon {:style "background-image:url('images/backgrounds/mars1.jpg')"}
      [:div.darken
       [:div.break]
       [:h2 {:style "font-family: Hack, monospace;"}
        "COMPUTER SCIENCE"]
       [:div.terminal
        "sol@"
        [:span {:style "color:aqua"}
         "sol-pc"] "-&gt; pwd"
        [:br] [:br] "/home/sol/Documents/" [:br] [:br] "sol@"
        [:span {:style "color:aqua"}
         "sol-pc"] "-&gt; Always building new features for my window manager. Loves hacking around."]]]
     [:div.abouticon
      {:style "background-image:url('images/backgrounds/mars2.jpg'); font-family: 'Computer Modern', serif;"}
      [:div.darken
       [:div.break]
       [:h2 {:style "font-family:'Computer Modern', serif; font-weight: normal;"}
        "MATHEMATICS"]
       [:div.math-text
        "Studying applied mathematics. Algorithm enthusiast. Doesn't ask \"How do we do it?\" but \"How do we do it " [:i "optimally?\""]]]]
     [:div.abouticon
      {:style "background-image:url('images/backgrounds/mars3.jpg')"}
      [:div.darken
       [:div.break]
       [:h2 "PHYSICS"]
       [:div.math-text
        {:style "font-family: 'Libre Baskerville'; font-size: 14px;"}
        "Studying physics out of pure curiousity. Interested in quantum mechanics, and super interested in quantum computing. Worked with computational simulations, fun!"]]]]
    [:div.experience
     [:div.break {:style "background-color: #2f363a;"}]
     [:h2 "WORK EXPERIENCE"]
     [:div.experienceflex
      [:img {:src "images/asseen/fh.png" :style "width: 200px; height: 200px;"}]]]
    [:div.asseen
     [:div.colorlayer
      [:div.break]
      [:h2 "AS SEEN"]
      (elem/link-to {:target "_blank"}
                    "http://dailyorange.com/2016/11/super-smash-bros-builds-community-across-central-new-york/")]]]))

(defn su-cal
  []
  (page-template
   {:js "/scripts/su-cal.js"}
   [:div.blog
    [:h1 "ical your schedule"]
    [:div.content [:ul
                   [:li "Log into MySlice, click on \"View Class Schedule\""]
                   [:li "Click \"View Printer Friendly Version\""]
                   [:li "Copy the entire page (Ctrl+A then Ctrl+C)"]
                   [:li "Paste into the field below."]
                   [:li "Click the button, save the file as a .ics file (something with .ics after it's name, e.g. "
                    [:code.tidbit "cal.ics"] "."]
                   "Then you can import this file into any calendar appliation you'd like."
                   [:br]
                   "NOTE: I " [:strong "STRONGLY"]
                   " recommend making a new calendar on Google Calendar before importing this. Just in case something breaks."]]
    (form/text-area {:id "cal-raw"} "cal")
    [:button {:onclick "window.open('/su-cal-gen?cal=' + encodeURI(document.getElementById('cal-raw').value))"
              :style "display: block; margin: auto;"}
     "get ics"]]))
