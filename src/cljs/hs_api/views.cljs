(ns hs-api.views
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [hs-api.client :as client]
   [hs-api.components :as c]))

(defnc list-patients []
  (let [[patients set-patients] (hooks/use-state nil)
        [error set-error] (hooks/use-state nil)]
    (do
      (hooks/use-effect [] (client/get-patients set-patients set-error))
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

(defnc edit-patient [] (d/div "Edit"))
