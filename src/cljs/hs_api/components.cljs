(ns hs-api.components
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   ["react-router-dom" :as router]))

(defnc app-header []
  (d/div
   {:class-name "row"}
   (d/h2 "HS")))

(defnc patients-header []
  (let [[search set-search] (router/useSearchParams)
        [query set-query] (hooks/use-state (or (js/search.get "query") ""))]
    (d/div
     {:class-name "row"}
     (d/div
      {:class-name "ten columns"}
      (d/input {:type "text"
                :class-name "u-full-width"
                :value query
                ;; TODO: On keyDown enter
                :on-change #(set-query (.. % -target -value))})
      ;; TODO: Layout
      (d/button {:class-name "button button-primary"
                 :on-click #(set-search #js {"query" query})} "Search"))
     (d/div
      {:class-name "two columns"}
      ($ router/Link {:to "/patients/new"}
         (d/button {:class-name "u-pull-right button button-primary"} "New"))))))

(defnc name-input [{:keys [value on-change]}]
  (d/input {:id "name"
            :name "name"
            :type "text"
            :class-name "u-full-width"
            :value value
            :on-change on-change}))

(defnc gender-input [{:keys [value on-change]}]
  (d/select {:id "gender"
             :name "gender"
             :class-name "u-full-width"
             :value value
             :on-change on-change}
            (d/option {:disabled true} "")
            (d/option {:value "M"} "Male")
            (d/option {:value "F"} "Female")))

(defnc date-of-birth-input [{:keys [value on-change]}]
  (d/input {:id "date-of-birth"
            :name "date-of-birth"
            :type "date"
            :class-name "u-full-width"
            :value value
            :on-change on-change}))

(defnc address-input [{:keys [value on-change]}]
  (d/input {:id "address"
            :name "address"
            :type "text"
            :class-name "u-full-width"
            :value value
            :on-change on-change}))

(defnc oms-input [{:keys [value on-change]}]
  (d/input {:id "oms"
            :name "oms"
            :type "text"
            :class-name "u-full-width"
            :value value
            :on-change on-change}))

(def empty-patient {:name "Test"
                    :gender "M"
                    :date-of-birth ""
                    :address "Test address"
                    :oms "1234567890123456"})

(defnc patient-form [{:keys [on-submit patient]}]
  (let [[form set-form]
        (hooks/use-state (or patient empty-patient))]
    (d/form
     (d/div {:class-name "row"}
            (d/div {:class-name "twelve columns"}
                   (d/label {:for "name"} "Name")
                   ($ name-input {:value (:name form)
                                  :on-change #(set-form assoc :name (.. % -target -value))})))
     (d/div {:class-name "row"}
            (d/div {:class-name "six columns"}
                   (d/label {:for "gender"} "Gender")
                   ($ gender-input {:value (:gender form)
                                    :on-change #(set-form assoc :gender (.. % -target -value))}))
            (d/div {:class-name "six columns"}
                   (d/label {:for "date-of-birth"} "Date of birth")
                   ($ date-of-birth-input {:value (:date-of-birth form)
                                           :on-change #(set-form assoc :date-of-birth (.. % -target -value))})))
     (d/div {:class-name "row"}
            (d/div {:class-name "six columns"}
                   (d/label {:for "address"} "Address")
                   ($ address-input {:value (:address form)
                                     :on-change #(set-form assoc :address (.. % -target -value))}))
            (d/div {:class-name "six columns"}
                   (d/label {:for "oms"} "OMS")
                   ($ oms-input {:value (:oms form)
                                 :on-change #(set-form assoc :oms (.. % -target -value))})))
     (d/hr)
     (d/div {:class-name "row"}
            (d/div {:class-name "six columns"}
                   ($ router/Link {:to "/patients"}
                      (d/button {:class-name "button u-full-width"}
                                "Cancel")))
            (d/div {:class-name "six columns"}
                   (d/button {:class-name "button button-primary u-full-width"
                              :type "button"
                              :on-click #(on-submit form)}
                             "Save"))))))

(defnc patient-row
  [{:keys [id name oms address]}]
  (d/tr
   (d/td (d/a {:href (str "#/patients/" id)} name))
   (d/td oms)
   (d/td address)))

(defnc patients-table [{:keys [patients]}]
  (d/div
   (d/table
    {:class-name "patients-table u-full-width"}
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
