(ns friends.db
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]))

(mg/connect!)
(mg/set-db! (mg/get-db "friends"))

(defn insert-friend [f]
  (mc/save "friends" f))

(defn get-friends []
  (mc/find-maps "friends"))
