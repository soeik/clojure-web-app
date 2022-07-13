(ns app.views
  (:require
   [re-frame.core :as re-frame]
   [app.subs :as subs]))

(defn test-button []
  [:button
   {:on-click #(re-frame.core/dispatch [:change-name "OLOLO"])}
   "YAY"])

(defn fetch-button []
  [:button
   {:on-click #(re-frame.core/dispatch [:get-patients {}])}
   "FETCH"])


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        patients-count (re-frame/subscribe [::subs/patients-count])]
    [:div
     [test-button]
     [fetch-button]
     [:h1
      "Hello from " @name]
     [:hr]
     [:h4 @patients-count " patients fetched"]
     ]))
