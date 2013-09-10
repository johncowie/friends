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
  (mc/remove-by-id "friends" (ObjectId. id))
  )

(defn get-friends [id]
  (mc/find-maps "friends" {:user id}))
