(defproject friends "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]
                 [com.novemberain/monger "1.4.1"]
                 [com.cemerick/friend "0.1.2"]
                 ]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler friends.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
