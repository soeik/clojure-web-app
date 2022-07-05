(ns hs-api.views
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   ["react-router-dom" :as router]
   [goog.object :as gobj]
   [hs-api.client :as client]
   [hs-api.components :as c]
   [hs-api.styles :as styles]
   [hs-api.patient :refer [patient-valid?]]
   [hs-api.util :refer [use-request]]))

(defnc list-patients []
  (let [[search] (router/useSearchParams)
        [search-patients searching patients error] (use-request client/search-patients)
        query (js/search.get "query")
        gender (js/search.get "gender")
        date-of-birth (js/search.get "date-of-birth")]
    (do
      (hooks/use-effect [search] (search-patients {:query query :gender gender :date-of-birth date-of-birth}))
      (d/div {:class-name (styles/search-page)}
       ($ c/patients-filter)
       (if searching ($ c/loading-page)
           (cond
             (some? patients) ($ c/patients-table
                                 {:patients patients}
                                 ($ c/table-sorting))
             (some? error) ($ c/error-page
                              {:full-width true
                               :error (str
                                       "Failed to perform search: "
                                       (:status-text error))})))))))

(defnc new-patient []
  (let [[create-patient in-progress result error] (use-request client/create-patient)
        error-message (if (some? error) (str "Failed to create patient: " (:status-text error)) nil)
        success-message (if (some? result) "Patient successfully created" nil)]
    (d/div {:class-name (styles/page-content)}
           (d/div {:class-name (styles/page-title)}
                  (d/h4 "New patient"))
           ($ c/patient-form
              {:key (:id result)
               :in-progress in-progress
               :error error-message
               :success success-message
               :on-submit create-patient}))))

(defnc edit-patient []
  (let [params (router/useParams)
        id (gobj/get params "id")
        navigate (router/useNavigate)
        [get-patient loading patient get-patient-error] (use-request client/get-patient-by-id)
        [delete-patient deleting deleted delete-patient-error] (use-request (partial client/delete-patient id))
        [update-patient updating updated update-patient-error] (use-request (partial client/update-patient id))
        error-message (cond
                        (some? update-patient-error)
                        (str "Failed to update patient: " (:status-text update-patient-error))
                        (some? delete-patient-error)
                        (str "Failed to delete patient: " (:status-text delete-patient-error)))
        success-message (if (some? updated) "Patient successfully updated" nil)]
    (do
      (hooks/use-effect [] (get-patient id))
      (d/div {:class-name (styles/page-content)}
             (cond
               (some? patient)
               (d/div
                (d/div
                 {:class-name (styles/page-title)}
                 (d/h4 (:name patient))
                 (d/button
                  {:class-name (styles/icon-button)
                   :on-click (fn [] (delete-patient #(navigate "/patients")))}
                  (d/i {:class-name (styles/icon :bin)})))
                ($ c/patient-form
                   {:key id
                    :patient patient
                    :in-progress updating
                    :error error-message
                    :success success-message
                    :on-submit update-patient}))
               (some? get-patient-error)
               ($ c/error-page
                  {:title "Failed to load patient"
                   :error (str (:status get-patient-error) " " (:status-text get-patient-error))})
               loading
               ($ c/loading-page))))))
