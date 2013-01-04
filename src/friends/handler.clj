(ns friends.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as ring-response]
            [ring.middleware.session :as ring-session]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [friends.db :as db]))

(defn friend-list [username]
  (hiccup/html
   [:h2 "My Friends"]
   [:ul
    (for [f (db/get-friends username)]
      [:li (:name f)
       (form/form-to [:delete (format "/friends/%s" (:_id f))]
          (form/submit-button "Remove Friend"))])]))

(defn homepage [session]
  (hiccup/html
   [:h1 (format "Hello %s, Here are your friends" (session :username))]
   (form/form-to [:post "/friends"]
                    (form/label "friendname" "Friend's Name: ")
                    (form/text-field "friendname" :friendname)
                    (form/submit-button "Add Friend"))
   (friend-list (session :username))))

(defn loginpage []
  (hiccup/html
   [:h1 "Login"]
   (form/form-to [:post "/"]
                 (form/label "username" "Username: ")
                 (form/text-field "username" :username)
                 (form/submit-button "Login"))))

(defn login [username]
  (let [user (db/find-user username)]
    (if (nil? user)
      (ring-response/redirect "/")
      (assoc (ring-response/redirect "/friends") :session {:username username}))))

(defn add-friend [session params]
  (db/insert-friend {:username (session :username) :name (params :friendname)})
  (ring-response/redirect "/friends"))

(defn delete-friend [id] (db/delete-friend id) (ring-response/redirect "/"))

(defn print-handler [app]
  (fn [request]
    (println (format "INCOMING REQUEST: %s" request))
    (println (format "SESSION: " (request :session)))
    (app request)))

(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "admin_password")
                    :roles #{::admin}}
            "jane" {:username "jane"
                    :password (creds/hash-bcrypt "user_password")
                    :roles #{::user}}})



(defroutes app-routes
  (GET "/" [] (ring-response/response (loginpage)))
  (POST "/" {params :params} (login (params :username)))
  (GET "/friends" {session :session} (homepage session))
  (POST "/friends" {params :params session :session} (add-friend session params))
  (DELETE "/:id" [id] (delete-friend id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (ring-session/wrap-session)
      (print-handler)))
