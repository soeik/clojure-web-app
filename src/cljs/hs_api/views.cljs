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
        [search-patients searching patients error] (use-request client/search-patients)]
    (do
      (hooks/use-effect [search] (let [query (js/search.get "query")]
                                   (search-patients {:query query})))
      (d/div {:class-name (styles/search-page)}
       ($ c/patients-filter)
       (if searching ($ c/loading-page)
           (cond
             (some? patients) ($ c/patients-table {:patients patients})
             (some? error) ($ c/error-page
                              {:error (str
                                       "Failed to perform search: "
                                       (:status-text error))})))))))

;; TODO Handle success -> notify, navigate to the created entry, or clear the form
(defnc new-patient []
  (let [[create-patient in-progress result error] (use-request client/create-patient)
        error-text (if (some? error) (str "Failed to create patient: " (:status-text error)) nil)]
    (d/div {:class-name (styles/page-content)}
           (d/h4 "New patient")
           (d/hr)
           ($ c/patient-form
              {:in-progress in-progress
               :error error-text
               :on-submit create-patient}))))

;; TODO Handle succes -> notify
(defnc edit-patient []
  (let [params (router/useParams)
        id (gobj/get params "id")
        [get-patient loading patient get-patient-error] (use-request client/get-patient-by-id)
        [update-patient updating result update-patient-error] (use-request client/update-patient)]
    (do
      (hooks/use-effect [] (get-patient id))
      (d/div {:class-name (styles/page-content)}
       (cond
         (some? patient)
         (d/div
          (d/h4 (:name patient))
          (d/hr)
          ($ c/patient-form
             {:patient patient
              :in-progress updating
              :error update-patient-error
              :on-submit (partial update-patient id)}))

         (some? get-patient-error)
         ($ c/error-page
            {:error (str (:status get-patient-error) " " (:status-text get-patient-error))})

         loading
         ($ c/loading-page))))))
