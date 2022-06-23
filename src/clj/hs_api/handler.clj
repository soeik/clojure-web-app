(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.data.json :refer [wrap-json-request
                                               wrap-json-response]]
            [ring.middleware.defaults :refer :all]
            [ring.util.http-response :refer :all]
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
                                      (created (db/create-patient body))
                                      (bad-request "Invalid input")))
           (context "/:id" [id]
                    (GET "/" []
                         (let [patient (db/get-patient id)]
                           (if (some? patient)
                             (ok patient)
                             (not-found "Patient not found"))))
                    (PUT "/" {body :body} (if (patient-valid? body)
                                              (ok (db/update-patient id body))
                                              (bad-request "Invalid input")))
                    (DELETE "/" [] (ok (db/delete-patient id)))))
  (route/resources "/")
  (route/not-found "Not Found"))

;; TODO [prone.middleware :refer [wrap-exceptions]]
;; TODO dev [ring.middleware.reload :refer [wrap-reload]]
;; TODO Filtering
;; TODO Improve input validation
;; TODO Improve error handling
;; TODO Tests
;; FIXME: Error with the date field
(defn wrap-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (println (str "Caught exception: " (.getMessage e)))
           {:status 500
            :body {:message (str "Error occured: " (.getMessage e))}}))))

(def app
  (->
   app-routes
   (wrap-defaults site-defaults)
   (wrap-json-request :key-fn keyword)
   wrap-json-response
   wrap-exception))


(comment
  (uuid->str {:id 123})
  (app {:request-method :get, :uri "/api/patients"})
  (app {:request-method :get, :uri "/api/patients", :params {:query "frank"}})
  (app {:request-method :post, :uri "/api/patients", :body {:oms "1234567890123456",
                                                            :name "Frank Cowperwood",
                                                            :gender "M"
                                                            :address "Sundgauerstr. 123" }})
  (app {:request-method :put,
        :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66",
        :body {:name "Donald Trumpet",
               :address "Philadelphia avenue 171",
               :oms     5646576767 }})
  (app {:request-method :get, :uri "/api/patients/non-existings"})
  (app {:request-method :get, :uri "/api/patients/b1393a03-8453-4f65-8b58-fd5631e66d66"})
  (app {:request-method :delete, :uri "/api/patients/57c181d4-63f0-423a-8f57-96528b8a0695"}))
