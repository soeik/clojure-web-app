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
   [hs-api.util :refer [use-request remove-empty-values]]))


(defnc patients-page []
  (let [[search set-search] (router/useSearchParams)
        query (js/search.get "query")
        gender (js/search.get "gender")
        date-of-birth (js/search.get "date-of-birth")
        sort-column (js/search.get "sort-column")
        sort-order (js/search.get "sort-order")
        [search-patients searching patients error] (use-request client/search-patients)
        empty-filter {:query "" :gender "" :date-of-birth ""}
        [filter set-filter] (hooks/use-state {:query (or query "")
                                              :gender (or gender "")
                                              :date-of-birth (or date-of-birth "")})
        [sorting set-sorting] (hooks/use-state {:sort-column (or sort-column "name")
                                                :sort-order (or sort-order "asc")})
        on-reset-click #(do (set-filter empty-filter) (set-search #js {}))
        on-search-click #(set-search (clj->js (remove-empty-values filter)))]
    (do
      (hooks/use-effect [search sorting] (search-patients {:query query
                                                           :gender gender
                                                           :date-of-birth date-of-birth
                                                           :sort-column (:sort-column sorting)
                                                           :sort-order (:sort-order sorting)}))
      (d/div {:class-name (styles/search-page)}
             ($ c/patients-filter
                {:filter filter
                 :set-filter set-filter
                 :on-reset-click on-reset-click
                 :on-search-click on-search-click})
       (if searching ($ c/loading-page)
           (cond
             (some? patients)
             ($ c/patients-table
                {:patients patients}
                ($ c/table-sorting
                   {:sort-column (:sort-column sorting)
                    :sort-order (:sort-order sorting)
                    :on-sort-column-change #(set-sorting assoc :sort-column %)
                    :on-sort-order-change #(set-sorting assoc :sort-order %)}))
             (some? error)
             ($ c/error-page
                {:full-width true
                 :error (str
                         "Failed to perform search: "
                         (:status-text error))})))))))

(defnc new-patient-page []
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

(defnc edit-patient-page []
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
                  {:class-name "button"
                   :on-click (fn [] (delete-patient #(navigate "/patients")))}
                  "Delete"))
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
