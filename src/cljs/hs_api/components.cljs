(ns hs-api.components
  (:require
   ["react-router-dom" :as router]
   [helix.core :refer [defnc $ <>]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [hs-api.patient :refer [validate-patient]]
   [hs-api.styles :as styles]))

(defnc app-header []
  (d/div
   {:class-name (styles/app-header)}
   (d/h4 {:class-name (styles/app-title)} "HS")))

(def circle-loader
  (d/svg
   {:class-name (styles/circle-loader nil)
    :width "30"
    :height "30"
    :version "1.1"
    :xmlns "http://www.w3.org/2000/svg"}
   (d/circle
    {:cx "15"
     :cy "15"
     :r "10"})))

(def circle-loader-big
  (d/svg
   {:class-name (styles/circle-loader :primary)
    :width "40"
    :height "40"
    :version "1.1"
    :xmlns "http://www.w3.org/2000/svg"}
   (d/circle
    {:cx "20"
     :cy "20"
     :r "15"})))


(defnc progress-button [{:keys [text in-progress on-click]}]
  (d/button
   {:class-name "button-primary"
    :type "button"
    :on-click on-click}
   (d/div {:class-name (styles/progress-button-content)}
          (if in-progress circle-loader
              (d/span text)))))

(defnc loading-page [{:keys [title]}]
  (d/div {:class-name (styles/utility-page true)}
         (when-not (empty? title)
           (<>
            (d/h4 title)
            (d/hr)))
         (d/div {:class-name (styles/utility-page-content)}
          circle-loader-big)))

(defnc error-page [{:keys [title error full-width]}]
  (d/div {:class-name (styles/utility-page full-width)}
         (when-not (empty? title)
           (<>
            (d/h4 title)
            (d/hr)))
         (d/div {:class-name (styles/utility-page-content)}
                (d/h5 error))))


(defnc search-input [{:keys [value on-change]}]
  (d/input {:type "text"
            :name "search-query"
            :value value
            :placeholder "Search by name or OMS"
            ;; TODO: On keyDown enter
            :on-change on-change}))

(defnc name-input [{:keys [value on-change valid disabled]}]
  (d/input {:id "name"
            :class-name (if-not valid "invalid" nil)
            :name "name"
            :type "text"
            :value value
            :disabled disabled
            :on-change on-change}))

(defnc gender-input [{:keys [value on-change valid disabled]}]
  (d/select {:id "gender"
             :class-name (if-not valid "invalid" nil)
             :name "gender"
             :value value
             :disabled disabled
             :on-change on-change}
            (d/option {:value ""} "")
            (d/option {:value "M"} "Male")
            (d/option {:value "F"} "Female")))

(defnc date-of-birth-input [{:keys [value on-change valid disabled]}]
  (d/input {:id "date-of-birth"
              :class-name (if-not valid "invalid" nil)
              :name "date-of-birth"
              :type "date"
              :value value
              :disabled disabled
            :on-change on-change}))

(defnc address-input [{:keys [value on-change disabled]}]
  (d/input {:id "address"
            :name "address"
            :type "text"
            :value value
            :disabled disabled
            :on-change on-change}))

(defnc oms-input [{:keys [value on-change valid disabled]}]
  (d/input {:id "oms"
            :class-name (if-not valid "invalid" nil)
            :name "oms"
            :type "text"
            :value value
            :disabled disabled
            :on-change on-change}))


(defnc patients-filter
  [{:keys [filter
           set-filter
           on-reset-click
           on-search-click]}]
  (d/div {:class (styles/filters)}
         ($ search-input
            {:value (:query filter)
             :on-change #(set-filter
                          (assoc filter :query (.. % -target -value)))})
         (d/div
          (d/label
           {:class-name (styles/filter-label)}
           "Gender")
          ($ gender-input
             {:value (:gender filter)
              :on-change #(set-filter
                           (assoc filter :gender (.. % -target -value)))}))
         (d/div
          (d/label
           {:class-name (styles/filter-label)}
           "Date of birth")
          ($ date-of-birth-input
             {:value (:date-of-birth filter)
              :on-change #(set-filter
                           (assoc filter :date-of-birth (.. % -target -value)))}))
         (d/div
          {:class-name (styles/filter-actions)}
          (d/button {:class-name "button button-primary"
                     :on-click on-search-click} "Search")
          (d/button {:class-name "button"
                     :on-click on-reset-click} "Reset"))))

(defnc patient-form [{:keys [on-submit patient in-progress editing error success]}]
  (let [empty-patient {:name "" :gender "" :date-of-birth "" :address "" :oms ""}
        [form set-form] (hooks/use-state (or patient empty-patient))
        [form-errors set-form-errors] (hooks/use-state nil)
        field-error (fn [key]
                      (if-let [msg (get form-errors key)]
                        (d/div {:class-name (styles/field-error)} msg)
                        nil))]
    (d/form {:class-name (styles/form)}
            (d/div {:class-name (styles/form-row)}
                   (d/div {:class-name (styles/form-group)}
                          (d/label {:for "name"} "Name")
                          ($ name-input {:value (:name form)
                                         :valid (nil? (:name form-errors))
                                         :disabled in-progress
                                         :on-change #(set-form assoc :name (.. % -target -value))})
                          (field-error :name)))
            (d/div {:class-name (styles/form-row)}
                   (d/div {:class-name (styles/form-group)}
                          (d/label {:for "gender"} "Gender")
                          ($ gender-input {:value (:gender form)
                                           :valid (nil? (:gender form-errors))
                                           :disabled in-progress
                                           :on-change #(set-form assoc :gender (.. % -target -value))})
                          (field-error :gender))
                   (d/div {:class-name (styles/form-group)}
                          (d/label {:for "date-of-birth"} "Date of birth")
                          ($ date-of-birth-input {:value (:date-of-birth form)
                                                  :valid (nil? (:date-of-birth form-errors))
                                                  :disabled in-progress
                                                  :on-change #(set-form assoc :date-of-birth (.. % -target -value))})
                          (field-error :date-of-birth)))
            (d/div {:class-name (styles/form-row)}
                   (d/div {:class-name (styles/form-group)}
                          (d/label {:for "address"} "Address")
                          ($ address-input {:value (:address form)
                                            :disabled in-progress
                                            :on-change #(set-form assoc :address (.. % -target -value))}))
                   (d/div {:class-name (styles/form-group)}
                          (d/label {:for "oms"} "OMS")
                          ($ oms-input {:value (:oms form)
                                        :valid (nil? (:oms form-errors))
                                        :disabled in-progress
                                        :on-change #(set-form assoc :oms (.. % -target -value))})
                          (field-error :oms)))
            (when error (d/div {:class-name (styles/form-error)} error))
            (when success (d/div {:class-name (styles/form-success)} success))
            (d/div {:class-name (styles/form-actions)}
                   ($ router/Link
                      {:to "/patients"}
                      (d/button {:class-name "button"} "Back"))
                   ($ progress-button
                      {:text "Save"
                       :in-progress in-progress
                       :on-click #(let [errors (validate-patient form)]
                                    (if (empty? errors)
                                      (on-submit form)
                                      (set-form-errors errors)))})))))

(defnc patient-row
  [{{:keys [id name oms date-of-birth gender address]} :patient}]
  (d/tr
   (d/td (d/a {:href (str "/patients/" id)} name))
   (d/td oms)
   (d/td date-of-birth)
   (d/td (case gender
           "M" "Male"
           "F" "Female"))
   (d/td address)))

(defnc table-sorting
  [{:keys [sort-column
           on-sort-column-change
           sort-order
           on-sort-order-change]}]
  (d/div {:class-name (styles/table-sorting)}
   (d/label "Sort by:")
   (d/select
    {:name "sort-column"
     :value sort-column
     :on-change #(on-sort-column-change (.. % -target -value))}
    (d/option {:value "name"} "Name")
    (d/option {:value "date-of-birth"} "Date of birth")
    (d/option {:value "gender"} "Gender"))
   (d/label "Sort order:")
   (d/select
    {:name "sort-order"
     :value sort-order
     :on-change #(on-sort-order-change (.. % -target -value))}
    (d/option {:value "asc"} "Ascending")
    (d/option {:value "desc"} "Descending"))))

(defnc patients-table [{:keys [patients children]}]
  (d/div {:class-name (styles/table-wrapper)}
         (d/div {:class-name (styles/search-header)}
                (d/div {:class-name (styles/table-info)}
                       children
                       (d/div
                        "Displaying patients: " (count patients)))
                ($ router/Link
                   {:class-name (styles/header-link)
                    :to "/patients/new"}
                   (d/button {:class-name "button-primary"} "New")))
   (d/table
    (d/thead
     (d/tr
      (d/th "Name")
      (d/th "OMS")
      (d/th "Date of birth")
      (d/th "Gender")
      (d/th "Address")))
    (d/tbody
     (for [patient patients]
       ($ patient-row {:key (patient :id)
                       :patient patient}))))))
