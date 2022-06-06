(ns hs-api.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))


(defn some-component []
  [:div
   [:h3 "I am react component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]])

(defn mount-root []
  (rdom/render [some-component] (.getElementById js/document "app")))
