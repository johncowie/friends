(ns friends.handler
  (:require
   [compojure.core :refer [defroutes GET POST DELETE]]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [ring.middleware.session :as session]
   [ring.util.response :refer [response redirect content-type]]
   [friends.views :as views]
   ;[sandbar.auth :as auth]
   [sandbar.stateful-session :as ss]
   [friends.db :as db]
   )
  (:import [twitter4j Twitter TwitterFactory]
           [twitter4j.conf PropertyConfiguration]))

(def auth-on true)

(defn html-response [html]
  (content-type (response html) "text/html"))

(def twitter-config
  (PropertyConfiguration. (clojure.java.io/input-stream "/Users/John/Dropbox/nuotltester.properties")))

(defn login [redirect-url]
  (let [twitter (. (TwitterFactory. twitter-config) (getInstance))
        callback-url "http://localhost:7777/callback"
        request-token (. twitter (getOAuthRequestToken callback-url))]
    (ss/session-put! :twitter twitter)
    (ss/session-put! :request-token request-token)
    (redirect (. request-token (getAuthenticationURL)))))

(defn callback [params]
  (let [
        twitter (ss/session-get :twitter)
        request-token (ss/session-get :request-token)
        verifier (:oauth_verifier params)]
    (. twitter (getOAuthAccessToken request-token verifier))
    (let [user (. twitter (showUser (. twitter (getId))))]
      (ss/session-put! :user {:handle (. user (getScreenName)) :name (. user (getName)) :id (. user (getId))})
      (redirect "/"))))

(defn logout [redirect-url]
  (assoc
   (redirect redirect-url)
   :cookies {"ring-session" {:value "" :max-age 0}}))

(defn auth [response]
  (if auth-on
    (if (nil? (ss/session-get :user))
      (redirect "/login")
      response)
    (do
      (ss/session-put! :user {:handle "testhandle" :name "Test Name" :id 1})
      response
      )))

(defn get-dashboard []
  (html-response (views/dashboard (ss/session-get :user))))

(defn get-friend-list []
  (html-response (views/friend-list (ss/session-get :user))))

(defn add-friend [params]
  (db/insert-friend (assoc params :user (:id (ss/session-get :user))))
  (redirect "/friend-list")
  )

(defn delete-friend [params]
  (db/delete-friend (:id params))
  (redirect "/friend-list")
  )

(defroutes app-routes
  (GET "/" [] (auth (get-dashboard)))
  (GET "/login" [] (html-response (views/login)))
  (POST "/login" [] (login "/"))
  (POST "/logout" []  (logout "/"))
  (GET "/callback" {params :params} (callback params))
  (GET "/friend-list" [] (auth (get-friend-list)))
  (POST "/friend-list" {params :params} (auth (add-friend params)))
  (POST "/friend-list/delete" {params :params} (auth (delete-friend params)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (ss/wrap-stateful-session)
      ))
