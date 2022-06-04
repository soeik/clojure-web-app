(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.http-response :refer :all]
            [hs-api.patient :refer [patient-valid?]]
            [hs-api.db :as db]))

(defroutes app-routes
  (context "/api/patients" []
           (GET "/" [] (ok (db/get-all-patients)))
           (POST "/" {body :body} (if (patient-valid? body)
                                      (created (db/create-patient body))
                                      (bad-request "Invalid input")))
           (context "/:id" [id]
                    (GET "/" [] (ok (db/get-patient id)))
                    (PUT "/" {body :body} (if (patient-valid? body)
                                              (ok (db/update-patient id body))
                                              (bad-request "Invalid input")))
                    (DELETE "/" [] (ok (db/delete-patient id)))))
  (route/resources "/")
  (route/not-found "Not Found"))

;; TODO Filtering
;; TODO Improve input validation
;; TODO Improve error handling
;; TODO Tests
(defn wrap-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (println (str "Caught exception: " (.getMessage e)))
           {:status 500
            :body {:message (str "Error occured: " (.getMessage e))}
            }))))

(def app
  (->
   (wrap-json-response app-routes)
   wrap-exception))


(comment
  (app {:request-method :get, :uri "/api/patients"})
  (app {:request-method :post, :uri "/api/patients", :body {:oms 1234567890123456,
                                                        :name "Frank Cowperwood",
                                                        :address "Sundgauerstr. 123" }})
  (app {:request-method :put,
        :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66",
        :body {:name "Donald Trumpet",
               :address "Philadelphia avenue 171",
               :oms     5646576767 }})
  (app {:request-method :get, :uri "/api/patients/non-existings"})
  (app {:request-method :get, :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66"})
  (app {:request-method :delete, :uri "/api/patients/57c181d4-63f0-423a-8f57-96528b8a0695"}))
