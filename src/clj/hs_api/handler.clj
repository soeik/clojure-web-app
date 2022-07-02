(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.data.json :refer [wrap-json-request wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hs-api.patient :refer [patient-valid?]]
            [hs-api.db :refer [db-client]]))

(defn entry->dto [col]
  (as-> col v
      (update v :date-of-birth str)
      (update v :id str)))

(defroutes app-routes
  (context "/api/patients" []
           (GET "/" {:keys [db query-params]}
                (let [ query (get query-params "query")
                      gender (get query-params "gender")
                      date-of-birth (get query-params "date-of-birth")
                      results ((db :search-patients) query gender date-of-birth)]
                  {:status 200 :body (map entry->dto results)}))
           (POST "/" {:keys [db body]} (if (patient-valid? body)
                                    {:status 201 :body (entry->dto ((db :create-patient) body))}
                                    {:status 400 :body "Invalid input"}))
           (context "/:id" [id]
                    (GET "/" {:keys [db]}
                         (let [patient ((db :get-patient) id)]
                           (if (some? patient)
                             {:status 200 :body (entry->dto patient)}
                             {:status 404 :body "Patient not found"})))
                    (PUT "/" {:keys [db body]} (if (patient-valid? body)
                                            (let [affected-rows ((db :update-patient) id body)]
                                              (if (= affected-rows 1)
                                                {:status 200 :body {:id id}}
                                                {:status 400 :body "Failed to update patient"}))
                                            {:status 400 :body "Invalid input"}))
                    (DELETE "/" {:keys [db]}
                            (let [affected-rows ((db :delete-patient) id)]
                              ;; TODO If affected more than 1 row?
                              (if (= affected-rows 1)
                                {:status 200 :body {:id id :deleted true}}
                                {:status 400 :body "Failed to delete patient"})))))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-db [f db]
  (fn [req]
    (f (assoc req :db db))))

;; TODO Search by name case insensitive
;; TODO Tests
;; TODO Date in this century
;; TODO Serve index file properly

(defn create-app [db]
  (->
   app-routes
   (wrap-db db)
   (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
   (wrap-json-request :key-fn keyword)
   wrap-json-response))

(def app (create-app db-client))

(comment
  (def patient-id "1cc3719a-6470-4a81-84f6-d6c340add5036")
  (entry->dto {:id 123})
  ((create-app db-client) {:request-method :get, :uri "/api/patients"})
  (app {:request-method :get, :uri "/api/patients", :params {:query "frank"}})
  (app {:request-method :post, :uri "/api/patients", :body {:oms "1234567890123456",
                                                            :name "Frank Cowperwood",
                                                            :gender "M"
                                                            :address "Sundgauerstr. 123" }})
  (app {:request-method :put,
        :uri "/api/patients/1cc3719a-6470-4a81-84f6-d6340add5036"
        :body {:name "Donald Trumpet",
               :address "Philadelphia ave."
               :gender "M"
               :oms "1234567890123456"}})
  (app {:request-method :get, :uri "/api/patients/non-existings"})
  (app {:request-method :get, :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66"})
  (app {:request-method :delete, :uri "/api/patients/57c181d4-63f0-423a-8f57-96528b8a0695"}))
