(ns friends.views
  (:require
   [hiccup.core :refer [html]]
   [hiccup.form :as form]
   [hiccup.page :refer [html5 include-css include-js]]))

(defn navbar []
  [:div {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container"}
    [:div {:class "navbar-header"}
     [:button {:type "button" :class "navbar-toggle" :data-toggle "collapse" :data-target ".navbar-collapse"}
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      ]
     [:a {:class "navbar-brand" :href= "#"} "Friends"]
     ]
    [:div {:class "collapse navbar-collapse"}

     [:ul {:class "nav navbar-nav"}
      ;[:li [:a {:href "#events"} "Events"]]
      ;[:li [:a {:href "#admin"} "Admin"]]
      ;[:li [:a {:href "#faq"} "FAQ"]]
      ]
     ]
    ]
   ]
  )

(defn wrap [title & content]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.css" "/css/custom.css")
          (include-js "/js/jquery.min.js" "/js/bootstrap.js")
         ]
         [:body
          (navbar)
          [:div {:class "container"}
           [:div {:class "main"}
            content]
           ]
         ]))

(defn login []
  (wrap "Login"
     [:h1 "Heya, you're gonna need to log in."]
     [:form {:method "post"}
      [:input {:type "submit" :value "login"}]
      ]))

(defn dashboard [user]
  (wrap "Dashboard"
   [:h2 (format "Hello %s (%s)" (:name user) (:handle user))]
   [:form {:method "post" :action "/logout"}
    (form/submit-button :logout)
    ]))
