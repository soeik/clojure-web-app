(ns hs-api.client
  (:require [ajax.core :refer [GET POST]]))

(defn get-patients [handler error-handler]
  (GET "/api/patients" {:handler handler
                        :error-handler error-handler
                        :response-format :json
                        :keywords? true}))

(defn create-patient [patient handler error-handler]
  (POST "/api/patients" {:params patient
                         :handler handler
                         :error-handler error-handler
                         :format :json
                         :response-format :json
                         :keywords? true}))
