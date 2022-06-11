(ns hs-api.views
  (:require
   [helix.core :refer [defnc $]]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [hs-api.http :refer [request]]
   [hs-api.components :as c]))

(defnc list-patients []
  (let [[patients set-patients] (hooks/use-state [])]
    (do
      (hooks/use-effect [] (request :get "/api/patients" set-patients))
      ($ c/patients-table {:patients patients}))))

(defnc new-patient []
  (d/div
   ($ c/patient-form)))

(defnc edit-patient [] (d/div "Edit"))
