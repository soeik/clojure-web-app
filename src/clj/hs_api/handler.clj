(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.data.json :refer [wrap-json-request wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hs-api.patient :refer [patient-valid?]]
            [hs-api.db :as db]))

(defn entry->dto [col]
  (as-> col v
      (update v :date-of-birth str)
      (update v :id str)))

(defroutes app-routes
  (context "/api/patients" []
           (GET "/" {params :query-params}
                (let [query (get params "query")
                      gender (get params "gender")
                      date-of-birth (get params "date-of-birth")
                      results (db/search-patients query gender date-of-birth)]
                  {:status 200 :body (map entry->dto results)}))
           (POST "/" {body :body} (if (patient-valid? body)
                                    {:status 201 :body (entry->dto (db/create-patient body))}
                                    {:status 400 :body "Invalid input"}))
           (context "/:id" [id]
                    (GET "/" []
                         (let [patient (db/get-patient id)]
                           (if (some? patient)
                             {:status 200 :body (entry->dto patient)}
                             {:status 404 :body "Patient not found"})))
                    (PUT "/" {body :body} (if (patient-valid? body)
                                            (let [affected-rows (db/update-patient id body)]
                                              (if (= affected-rows 1)
                                                {:status 200 :body {:id id}}
                                                {:status 400 :body "Failed to update patient"}))
                                            {:status 400 :body "Invalid input"}))
                    (DELETE "/" [] {:status 200 :body (db/delete-patient id)})))
  (route/resources "/")
  (route/not-found "Not Found"))

;; TODO Validate date of birth
;; TODO Delete entry
;; TODO Tests

(def app
  (->
   app-routes
   (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
   (wrap-json-request :key-fn keyword)
   wrap-json-response))


(comment
  (entry->dto {:id 123})
  (app {:request-method :get, :uri "/api/patients"})
  (app {:request-method :get, :uri "/api/patients", :params {:query "frank"}})
  (app {:request-method :post, :uri "/api/patients", :body {:oms "1234567890123456",
                                                            :name "Frank Cowperwood",
                                                            :gender "M"
                                                            :address "Sundgauerstr. 123" }})
  (update-patient "86f6e38b-fa0a-40b8-bce5-903df0fb97d4"
                  {:id "b1393a03-8453-4f65-8b58-fd5631e66d66",
                   :name "Donald Trumpet",
                   :address "Philadelphia ave."
                   :gender "M"
                   :oms "5646576767" })
  (app {:request-method :put,
        :uri "/api/patients/86f6e38b-fa0a-40b8-bce5-903df0fb97d4",
        :body {:name "Donald Trumpet",
               :address "Philadelphia ave."
               :gender "M"
               :oms "1234567890123456"}})
  (app {:request-method :get, :uri "/api/patients/non-existings"})
  (app {:request-method :get, :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66"})
  (app {:request-method :delete, :uri "/api/patients/57c181d4-63f0-423a-8f57-96528b8a0695"}))
