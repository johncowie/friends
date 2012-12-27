(ns friends.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as ring-response]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [friends.db :as db]))

(defn friend-list []
  (hiccup/html
   [:h2 "My Friends"]
   [:ol
    (for [f (db/get-friends)]
      [:li (:name f)])]))

(defn homepage []
  (hiccup/html
   [:h1 "Friends"]
   (form/form-to [:post "/"]
                    (form/label "friendname" "Friend's Name: ")
                    (form/text-field "friendname" :friendname)
                    (form/submit-button "Add Friend"))
   (friend-list)))

(defroutes app-routes
  (GET "/" [] (homepage))
  (POST "/" {params :params} (db/insert-friend {:name (params :friendname)}) (ring-response/redirect "/"))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
