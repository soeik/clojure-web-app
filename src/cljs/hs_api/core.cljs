(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   [hs-api.http :refer [request]]))

(defnc patient-row
  [{:keys [patient]}]
  (d/tr
   {:key (patient :id)}
   (d/td (patient :name))
   (d/td (patient :oms))
   (d/td (patient :address))))

(defnc patients-table []
  (let [[patients set-patients] (hooks/use-state [])]
    (do
      (hooks/use-effect [] (request :get "/api/patients" set-patients))
      (d/div
       (d/table
        {:class-name "u-full-width"}
        (d/thead
         (d/tr
          (d/th "Name")
          (d/th "OMS")
          (d/th "Address")))
        (d/tbody
         (for [patient patients] ($ patient-row  {:patient patient}))))
       (d/div "Total rows: " (count patients))))))

(defnc app []
  (d/div
   {:class-name "container"}
   ($ patients-table)))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))

(comment
  ;; code samples
  ;; define components using the `defnc` macro
  (defnc greeting
    "A component which greets a user."
    [{:keys [name]}]
    ;; use helix.dom to create DOM elements
    (d/label "Hello, " (d/strong name) "!"))

  (defnc app-example []
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
                   :type "button"} "Submit something"))))))
