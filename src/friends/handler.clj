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
    (redirect (. request-token (getAuthenticationURL)))
    ))

(defn callback [params]
  (let [
        twitter (ss/session-get :twitter)
        request-token (ss/session-get :request-token)
        verifier (:oauth_verifier params)]
    (. twitter (getOAuthAccessToken request-token verifier))
    (let [user (. twitter (showUser (. twitter (getId))))]
      (ss/session-put! :user {:handle (. user (getScreenName)) :name (. user (getName)) :id (. user (getId))})
      (redirect "/")
      )))

(defn logout [redirect-url]
  (assoc
   (redirect redirect-url)
   :cookies {"ring-session" {:value "" :max-age 0}}
   ))

(defn auth [response]
  (if auth-on
    (if (nil? (ss/session-get :user))
      (redirect "/login")
      response)
    (do
      (ss/session-put! :user {:handle "testhandle" :name "Test Name" :id 1})
      response
      )
    ))

(defn get-dashboard []
  (html-response (views/dashboard (ss/session-get :user))))

(defroutes app-routes
  (GET "/" [] (auth (get-dashboard)))
  (GET "/login" [] (html-response (views/login)))
  (POST "/login" {params :params} (login "/"))
  (POST "/logout" []  (logout "/"))
  (GET "/callback" {params :params} (callback params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      (ss/wrap-stateful-session)
      ))
