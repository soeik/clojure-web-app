(ns hs-api.components
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   ["react-router-dom" :as router]))

(defnc app-header []
  (d/div
   {:class-name "row"}
   (d/h2 "HS")))

(defnc patients-header []
  (d/div
   {:class-name "row"}
   (d/div
    {:class-name "ten columns"}
    (d/input {:type "text" :class-name "u-full-width"}))
   (d/div
    {:class-name "two columns"}
    ($ router/Link {:to "/patients/new"}
       (d/button {:class-name "u-pull-right button button-primary"} "New")))))

(defnc patient-form []
  (d/form
   (d/div {:class-name "row"}
          (d/div {:class-name "twelve columns"}
                 (d/label {:for "name"} "Name")
                 (d/input {:id "name"
                           :name "name"
                           :type "text"
                           :class-name "u-full-width"})))
   (d/div {:class-name "row"}
          (d/div {:class-name "six columns"}
                 (d/label {:for "gender"} "Gender")
                 (d/select {:id "gender"
                            :name "gender"
                            :class-name "u-full-width"}
                           (d/option {:disabled true :selected true} "")
                           (d/option {:value "male"} "Male")
                           (d/option {:value "female"} "Female")))
          (d/div {:class-name "six columns"}
                 (d/label {:for "dateOfBirth"} "Date of birth")
                 (d/input {:id "dateOfBirth"
                           :name "dateOfBirth"
                           :type "date"
                           :class-name "u-full-width"})))
   (d/div {:class-name "row"}
          (d/div {:class-name "six columns"}
                 (d/label {:for "address"} "Address")
                 (d/input {:id "address"
                           :name "address"
                           :type "text"
                           :class-name "u-full-width"}))
          (d/div {:class-name "six columns"}
                 (d/label {:for "oms"} "OMS")
                 (d/input {:id "oms"
                           :name "oms"
                           :type "text"
                           :class-name "u-full-width"})))
   (d/hr)
   (d/div {:class-name "row"}
          (d/div {:class-name "six columns"}
                 ($ router/Link {:to "/patients"}
                    (d/button {:class-name "button u-full-width"}
                              "Cancel")))
          (d/div {:class-name "six columns"}
                 (d/button {:class-name "button button-primary u-full-width"}
                           "Save")))))

(defnc patient-row
  [{:keys [id name oms address]}]
  (d/tr
   (d/td name)
   (d/td oms)
   (d/td address)))

(defnc patients-table [{:keys [patients]}]
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
   (d/div "Total rows: " (count patients))))



(comment
  ;; code samples
  ;; define components using the `defnc` macro
  (defnc wrapper [{:keys [children]}]
      ;; (js/console.log "I am wrapper")
      (d/div {:style {:color "pink"}} "children: " children))

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
