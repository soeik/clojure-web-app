(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   ["react-dom" :as rdom]))

;; define components using the `defnc` macro
(defnc greeting
  "A component which greets a user."
  [{:keys [name]}]
  ;; use helix.dom to create DOM elements
  (d/label "Hello, " (d/strong name) "!"))

(defnc app []
  (let [[state set-state] (hooks/use-state {:name "Helix User"})]
    (d/div
     {:class-name "container"}
     (d/div
      {:class-name "row"}
      (d/h1 "Welcome!"))
     ;; create elements out of components
     (d/div
      {:class-name "row"}
      ($ greeting {:name (:name state)}))
     (d/div
      {:class-name "row"}
      (d/input {:class-name "u-full-width"
                :type "text"
                :value (:name state)
                :on-change #(set-state assoc :name (.. % -target -value))}))
     (d/div
      {:class-name "row"}
      (d/button {:class-name "button-primary"
                 :type "button"} "Submit something")))))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))
