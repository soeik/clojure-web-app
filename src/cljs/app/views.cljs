(ns app.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [app.subs :as subs]
   [app.components :as c]
   [app.styles :as s]))

(defn edit-patient []
  (let [modal-visible @(subscribe [::subs/modal-visible])
        patient-id @(subscribe [::subs/patient-id])
        patient @(subscribe [::subs/patient])
        in-progress @(subscribe [::subs/in-progress])
        api-errors @(subscribe [::subs/api-errors])
        get-patient-error (:get-single-patient api-errors)
        ;; TODO page title depending on edit / new
        ;; TODO Title when error
        page-title (if (nil? patient-id) "New patient" (:name patient))]
    (when modal-visible
      (c/modal-window
       [:div
        [:div {:class (s/page-title)}
         [:h4 page-title]]
        (if (:get-single-patient in-progress)
          (c/loading-page nil)
          (if (some? get-patient-error)
            (c/error-page {:error "Failed to load patient"})
            (c/patient-form
             {:patient patient
              :in-progress (or (:create-patient in-progress)
                               (:update-patient in-progress))
              :error (or (:create-patient api-errors)
                         (:update-patient api-errors))
              :on-field-change (fn [field-name]
                                 (fn [e]
                                   (dispatch [:set-patient-form [field-name (.. e -target -value)]])))
              :on-submit #(dispatch (if (nil? patient-id)
                                      [:create-patient]
                                      [:update-patient patient-id]))
              :on-cancel #(dispatch [:set-modal-visible false])})))]))))

(defn main-panel []
  (let [in-progress @(subscribe [::subs/in-progress])
        patients @(subscribe [::subs/patients])
        filter @(subscribe [::subs/filter])
        set-filter #(dispatch [:set-filter %])
        reset-filter #(dispatch [:reset-filter nil])]
    [:div
     [:div {:class (s/search-header)}
      [:div {:class (s/table-info)}
       (c/search-filter {:filter filter
                         :on-filter-change set-filter
                         :on-filter-reset reset-filter})
       [:div "Displaying patients: " (count patients)]]
      [:button.button-primary
       {:on-click #(dispatch [:new-patient nil])}
       "New"]]
     ;; TODO debounce input
     (if (:get-patients in-progress)
       (c/loading-page nil)
       (c/patients-table
        patients
        #(dispatch [:edit-patient %])
        ))
     (edit-patient)
     ]))
