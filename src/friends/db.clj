(ns friends.db
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.joda-time]
   [clj-time.core :refer [now]])
  (:import [org.bson.types ObjectId]))

(mg/connect!)
(mg/set-db! (mg/get-db "friends"))

(defn insert-friend [f]
  (mc/save "friends" f))

(defn delete-friend [id]
  (mc/remove-by-id "friends" (ObjectId. id))
  )

(defn update-last-seen [id]
  (let [record (mc/find-map-by-id "friends" (ObjectId. id))]
    (mc/save "friends" (assoc record :last-seen (now)))))

(defn get-friends [id]
  (mc/find-maps "friends" {:user id}))
