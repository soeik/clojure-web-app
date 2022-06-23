(ns hs-api.views
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [hs-api.client :as client]
   [hs-api.components :as c]
   ["react-router-dom" :as router]
   [goog.object :as gobj]))

(defnc list-patients []
  (let [[search] (router/useSearchParams)
        [patients set-patients] (hooks/use-state nil)
        [error set-error] (hooks/use-state nil)]
    (do
      ;; TODO: No loading state when searching
      (hooks/use-effect [search] (client/search-patients
                                  (js/search.get "query")
                                  set-patients
                                  set-error))
      (hooks/use-effect [patients, error] (js/console.log patients error) )
      (d/div
       ($ c/patients-header)
       (cond
         ;; TODO: Loading and error components
         (some? patients) ($ c/patients-table {:patients patients})
         (some? error) (d/div (str "Error occurred: " (:status-text error)))
         :else (d/div "Loading"))))))

(defnc new-patient []
  (d/div
   ($ c/patient-form
      {:on-submit (fn [patient]
                    (client/create-patient
                     patient js/console.log
                     js/console.error))})))

(defnc edit-patient []
  (let [params (router/useParams)
        id (gobj/get params "id")
        [patient set-patient] (hooks/use-state nil)
        [error set-error] (hooks/use-state nil)]
    (do
      (hooks/use-effect [] (client/get-patient-by-id
                            id
                            set-patient
                            set-error))
      (hooks/use-effect [patient, error, params] (js/console.log id  patient error) )
      (d/div
       ($ c/patients-header)
       (cond
         ;; TODO: Loading and error components
         (some? patient) ($ c/patient-form
                            {:patient patient
                             :on-submit (fn [patient]
                                          ;; TODO: Fix error on submit
                                          (client/update-patient
                                           id
                                           patient
                                           js/console.log
                                           js/console.error))})
         (some? error) (d/div (str "Error occurred: " (:status-text error)))
         :else (d/div "Loading"))))))
