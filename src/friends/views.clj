(ns friends.views
  (:require
   [hiccup.core :refer [html]]
   [hiccup.form :as form]
   [hiccup.page :refer [html5 include-css include-js]]
   [friends.db :as db]
   ))

(defn navbar [user]
  [:div {:class "navbar navbar-inverse navbar-fixed-top"}
   [:div {:class "container"}
    [:div {:class "navbar-header"}
     [:button {:type "button" :class "navbar-toggle" :data-toggle "collapse" :data-target ".navbar-collapse"}
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      ]
     [:a {:class "navbar-brand" :href "/"} (if (nil? user) "Friends" (str (:name user) "'s friends" ))]
     ]
    [:div {:class "collapse navbar-collapse"}

     [:ul {:class "nav navbar-nav"}
      [:li [:a {:href "/friend-list"} "Friend List"]]
      ;[:li [:a {:href "#admin"} "Admin"]]
      ;[:li [:a {:href "#faq"} "FAQ"]]
      ]

     (if (nil? user)
       [:form {:class "navbar-form navbar-right" :action "/login" :method :post}
        [:button {:type :submit :class "btn btn-success" :name :login} "Login"]]
       [:form {:class "navbar-form navbar-right" :action "/logout" :method :post}
        [:button {:type :submit :class "btn btn-danger" :name :logout} "Logout"]]
       )
     ]
    ]
   ]
  )

(defn wrap [user title & content]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.css" "/css/custom.css")
          (include-js "/js/jquery.min.js" "/js/bootstrap.js")
         ]
         [:body
          (navbar user)
          [:div {:class "container"}
           [:div {:class "main"}
            content]
           ]
         ]))

(defn login []
  (wrap nil "Login"
     [:h1 "Heya, you're gonna need to log in."]
     [:form {:method "post"}
      [:button {:type :submit :class "btn btn-primary" :name :login} "Login"]
      ]))

(defn dashboard [user]
  (wrap user "Dashboard"
   [:h2 (format "Hello %s (%s)" (:name user) (:handle user))]
   [:form {:method "post" :action "/logout"}
    [:button {:type :submit :class "btn btn-primary" :name :logout} "Logout"]
    ]))

(defn friend-form []
  [:form {:class "form-inline" :role :form :method :post}
   [:div {:class "form-group"}
    [:label {:class "sr-only" :for :firstname} "Firstname"]
    [:input {:type :text :class "form-control" :name :firstname :placeholder "First name"}]
    ]
   [:div {:class "form-group"}
    [:label {:class "sr-only" :for :lastname} "Surname"]
    [:input {:type :text :class "form-control" :name :lastname :placeholder "Last name"}]
    ]
   [:button {:type :submit :class "btn btn-success"} "Add"]
   ])

(defn friend-list [user]
  (wrap user "My Friends"
        [:h2 "My Friends"]
        (friend-form)
        [:p
         [:table {:class :table}
          (for [x (db/get-friends (:id user))]
            [:tr
             [:td (str (:firstname x) " " (:lastname x))]
             [:td
              [:form {:class "form-inline" :role :form :method :post :action "/friend-list/delete"}
               [:input {:type :hidden :name :id :value (:_id x)}]
               [:button {:type :submit :class "btn btn-danger"} "Delete"]
               ]
              ]
             ])]]))
