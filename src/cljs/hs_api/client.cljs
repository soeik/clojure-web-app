(ns hs-api.client
  (:require [ajax.core :refer [GET POST PUT]]))

(defn search-patients [params handler error-handler]
  (GET "/api/patients" {:url-params (into {} (filter second params))
                        :handler handler
                        :error-handler error-handler
                        :response-format :json
                        :keywords? true}))

(defn get-patient-by-id [id handler error-handler]
  (GET (str "/api/patients/" id) {:handler handler
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

(defn update-patient [id patient handler error-handler]
  (PUT (str "/api/patients/" id) {:params patient
                                  :handler handler
                                  :error-handler error-handler
                                  :format :json
                                  :response-format :json
                                  :keywords? true}))
