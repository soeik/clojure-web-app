(ns app.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [app.subs :as subs]
   [app.components :as c]
   [app.styles :as s]))

(defn patient-panel []
  (let [patient-id @(subscribe [::subs/patient-id])
        in-progress @(subscribe [::subs/in-progress])
        api-errors @(subscribe [::subs/api-request-error])
        title (if (nil? patient-id) "New patient" "Edit patient")]
    (c/modal-window
     [:div
      [:div {:class (s/page-title)}
       [:h4 title]]
      (if (:get-single-patient in-progress)
        (c/loading-page nil)
        (if (some? (:get-single-patient api-errors))
          (c/error-page {:error "Failed to load patient"})
          (c/patient-form)))])))

(defn main-panel []
  (let [modal-visible @(subscribe [::subs/modal-visible])
        in-progress @(subscribe [::subs/in-progress])
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
       #_[:div "Displaying patients: " (count patients)]]
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
     (if modal-visible (patient-panel))]))
