(ns hs-api.components
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   [hs-api.http :refer [request]]))

(defnc app-header []
  (d/div
   {:class-name "row"}
   (d/div
    {:class-name "one column"}
    (d/h4 "HS"))
   (d/div
    {:class-name "eleven columns"}
    (d/button {:class-name "u-pull-right"} "New"))))

(defnc patient-row
  [{:keys [id name oms address]}]
  (d/tr
   (d/td name)
   (d/td oms)
   (d/td address)))

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
         (for [patient patients]
           ($ patient-row {:key (patient :id)
                           :id (patient :id)
                           :name (patient :name)
                           :oms (patient :oms)
                           :address (patient :address)}))))
       (d/div "Total rows: " (count patients))))))

(defnc new-patient [] (d/div "New"))

(defnc edit-patient [] (d/div "Edit"))


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
