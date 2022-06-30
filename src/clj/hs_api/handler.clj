(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.data.json :refer [wrap-json-request
                                               wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [ring.util.http-response :refer [ok
                                             bad-request
                                             not-found]]
            [hs-api.patient :refer [patient-valid?]]
            [hs-api.db :as db]))

(defn id->str [col]
  (update col :id str))

(defroutes app-routes
  (context "/api/patients" []
           (GET "/" {params :query-params}
                (let [query (get params "query")]
                  (ok (map id->str (if (some? query)
                                     (db/search-patients query)
                                     (db/get-all-patients))))))
           (POST "/" {body :body} (if (patient-valid? body)
                                    {:status 201 :body (id->str (db/create-patient body))}
                                    {:status 400 :body "Invalid input"}))
           (context "/:id" [id]
                    (GET "/" []
                         (let [patient (db/get-patient id)]
                           (if (some? patient)
                             (ok patient)
                             (not-found "Patient not found"))))
                    (PUT "/" {body :body} (if (patient-valid? body)
                                            (let [affected-rows (db/update-patient id body)]
                                              (if (= affected-rows 1)
                                                {:status 200 :body {:id id}}
                                                {:status 400 :body "Failed to update patient"}))
                                            {:status 400 :body "Invalid input"}))
                    (DELETE "/" [] (ok (db/delete-patient id)))))
  (route/resources "/")
  (route/not-found "Not Found"))

;; TODO dev [ring.middleware.reload :refer [wrap-reload]]
;; TODO Filtering
;; TODO Improve input validation
;; TODO Improve error handling
;; TODO Tests
;; FIXME: Error with the date field

(def app
  (->
   app-routes
   (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
   (wrap-json-request :key-fn keyword)
   wrap-json-response))


(comment
  (uuid->str {:id 123})
  (created {:foo "bar"})
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
