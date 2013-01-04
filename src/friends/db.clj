(ns friends.db
  (:require
   [monger.core :as mg]
   [monger.collection :as mc])
  (:import [org.bson.types ObjectId]))

(mg/connect!)
(mg/set-db! (mg/get-db "friends"))

(defn insert-friend [f]
  (mc/save "friends" f))

(defn delete-friend [id]
  (mc/remove-by-id "friends" (ObjectId. id)))

(defn insert-user [u]
  (mc/save "users" u))

(defn find-user [id]
  (mc/find-map-by-id "users" id))

(delete-friend "50dccdb63004a09ce0a92d64")

(defn get-friends [username]
  (mc/find-maps "friends" {:username username}))
