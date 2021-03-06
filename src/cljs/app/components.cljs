(ns app.components
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe dispatch]]
   [app.styles :as s]
   [app.subs :as subs]))

;; Shared components
(def circle-loader
  [:svg
   {:class-name (s/circle-loader nil)
    :width "30"
    :height "30"
    :version "1.1"
    :xmlns "http://www.w3.org/2000/svg"}
   [:circle
    {:cx "15"
     :cy "15"
     :r "10"}]])

(def circle-loader-big
  [:svg
   {:class (s/circle-loader :primary)
    :width "40"
    :height "40"
    :version "1.1"
    :xmlns "http://www.w3.org/2000/svg"}
   [:circle
    {:cx "20"
     :cy "20"
     :r "15"}]])

(defn loading-page [title]
  [:div {:class-name (s/utility-page true)}
   (when-not (empty? title)
     [:h4 title]
     [:hr])
   [:div {:class-name (s/utility-page-content)}
    circle-loader-big]])

(defn error-page [{:keys [title error full-width]}]
  [:div {:class-name (s/utility-page full-width)}
   (when-not (empty? title)
     [:div
      [:h4 title]
      [:hr]])
   [:div {:class-name (s/utility-page-content)}
    [:h5 error]]])


(defn modal-window [children]
  [:div {:class (s/modal-overlay)}
   [:div {:class (s/modal-content)}
    children]])

(defn progress-button [{:keys [text in-progress on-click]}]
  [:button
   {:class "button-primary"
    :type "button"
    :on-click on-click}
   [:div {:class-name (s/progress-button-content)}
    (if in-progress circle-loader
        [:span text])]])

(defn search-filter [{:keys [filter on-filter-change on-filter-reset]}]
  [:div {:class (s/table-filter)}
   [:div
    [:label "Filter"]
    [:input {:type "text"
             :value (:query filter)
             :placeholder "Search by name or OMS"
             :name "query"
             :on-change #(on-filter-change [:query (.. % -target -value)])}]]
   [:div
    [:label "Gender"]
    [:select
     {:value (:gender filter)
      :on-change #(on-filter-change [:gender (.. % -target -value)])}
     [:option {:value ""} ""]
     [:option {:value "M"} "Male"]
     [:option {:value "F"} "Female"]]]
   [:div
    [:label "Sort by"]
    [:select
     {:value (:sort-column filter)
      :on-change #(on-filter-change [:sort-column (.. % -target -value)])}
     [:option {:value "name"} "Name"]
     [:option {:value "gender"} "Gender"]
     [:option {:value "date-of-birth"} "Date of birth"]]]
   [:div
    [:label "Sort order"]
    [:select
     {:value (:sort-order filter)
      :on-change #(on-filter-change [:sort-order (.. % -target -value)])}
     [:option {:value "asc"} "Ascending"]
     [:option {:value "desc"} "Descending"]]]
   [:button
    {:on-click on-filter-reset}
    "Reset"]])

(defn patients-table [patients on-patient-click]
  [:div {:class (s/table-wrapper)}
   [:table
    [:thead
     [:tr
      [:th "Name"]
      [:th "OMS"]
      [:th "Date of birth"]
      [:th "Gender"]
      [:th "Address"]]]
    [:tbody
     (for [patient patients]
       [:tr
        {:class (s/table-row)
         :key (:id patient)
         :on-click #(on-patient-click (:id patient))}
        [:td (:name patient)]
        [:td (:oms patient)]
        [:td (:date-of-birth patient)]
        [:td (get {"F" "Female" "M" "Male"} (:gender patient) "-")]
        [:td (:address patient)]
        ])]]])

;; Patient form
(defn patient-form []
  (let [patient @(subscribe [::subs/patient])
        in-progress (:submit-patient @(subscribe [::subs/in-progress]))
        api-errors @(subscribe [::subs/api-request-error])
        success @(subscribe [::subs/submit-patient-success])
        form-errors @(subscribe [::subs/form-errors])
        get-input-class #(when-not (nil? (form-errors %)) "invalid")
        on-field-change (fn [field-name]
                           (fn [e]
                             (dispatch [:set-patient-form [field-name (.. e -target -value)]])))
        on-submit #(dispatch [:submit-patient])
        on-cancel #(dispatch [:set-modal-visible false])
        field-error (fn [key]
                      (if-let [msg (get form-errors key)]
                        [:div {:class (s/field-error)} msg]
                        nil))]
    [:form {:class (s/form)}
     [:div {:class (s/form-row)}
      [:div {:class (s/form-group)}
       [:label {:for "name"} "Name"]
       [:input
        {:type "text"
         :name "name"
         :value (:name patient)
         :class (get-input-class :name)
         :disabled in-progress
         :on-change (on-field-change :name)}]
       (field-error :name)]]
     [:div {:class (s/form-row)}
      [:div {:class (s/form-group)}
       [:label {:for "gender"} "Gender"]
       [:select
        {:name "gender"
         :class (get-input-class :gender)
         :value (:gender patient)
         :disabled in-progress
         :on-change (on-field-change :gender)}
        [:option {:value ""} ""]
        [:option {:value "M"} "Male"]
        [:option {:value "F"} "Female"]
        ]
       (field-error :gender)]
      [:div {:class (s/form-group)}
       [:label {:for "date-of-birth"} "Date of birth"]
       [:input
        {:type "date"
         :name "date-of-birth"
         :value (:date-of-birth patient)
         :class (get-input-class :date-of-birth)
         :disabled in-progress
         :on-change (on-field-change :date-of-birth)}]
       (field-error :date-of-birth)]]
     [:div {:class (s/form-row)}
      [:div {:class (s/form-group)}
       [:label {:for "address"} "Address"]
       [:input
        {:type "text"
         :name "address"
         :value (:address patient)
         :class (get-input-class :address)
         :disabled in-progress
         :on-change (on-field-change :address)}]]
      [:div {:class (s/form-group)}
       [:label {:for "oms"} "OMS"]
       [:input
        {:type "text"
         :name "oms"
         :value (:oms patient)
         :class (get-input-class :oms)
         :disabled in-progress
         :on-change (on-field-change :oms)}]
       (field-error :oms)]]
     (when (:submit-patient api-errors)
       [:div {:class (s/form-error)} "Request failed"])
     (when success [:div {:class (s/form-success)} "Patient successfully saved"])
     [:div {:class (s/form-actions)}
      [:button.button
       {:type "button"
        :on-click on-cancel}
       "Close"]
      (progress-button
         {:text "Save"
          :in-progress in-progress
          :on-click on-submit})]]))
