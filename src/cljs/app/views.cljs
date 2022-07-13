(ns app.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [app.subs :as subs]
   [app.components :as c]
   [app.styles :as s]))

(defn edit-patient []
  (let [modal-visible @(subscribe [::subs/modal-visible])
        patient @(subscribe [::subs/patient])
        in-progress @(subscribe [::subs/in-progress])
        api-errors @(subscribe [::subs/api-errors])]
    (when modal-visible
      (c/modal-window
       [:div
        [:div {:class (s/page-title)}
         [:h4 "New patient"]]
        (c/patient-form
         {:patient patient
          :in-progress (:create-patient in-progress)
          :error (:create-patient api-errors)
          :on-field-change (fn [field-name]
                             (fn [e]
                               (dispatch [:set-patient-form [field-name (.. e -target -value)]])))
          :on-submit #(dispatch [:create-patient])
          :on-cancel #(dispatch [:set-modal-visible false])})]))))

(defn main-panel []
  (let [in-progress @(subscribe [::subs/in-progress])
        patients @(subscribe [::subs/patients])
        set-filter #(dispatch [:set-filter %])]
    [:div
     [:div {:class (s/search-header)}
      [:div {:class (s/table-info)}
       (c/search-filter set-filter)
       [:div "Displaying patients: " (count patients)]]
      [:button.button-primary
       {:on-click #(dispatch [:set-modal-visible true])}
       "New"]]
     ;; TODO debounce input
     (if (:get-patients in-progress)
       (c/loading-page nil)
       (c/patients-table patients))
     (edit-patient)
     ]))
