(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.http-response :refer :all]
            [hs-api.db :as db]))

(defroutes app-routes
  (context "/patients" []
           (GET "/" []
                (ok (db/get-all-patients)))
           (GET "/:id" [id]
                (ok (db/get-patient-by-id id))))
  (route/not-found "Not Found"))

;; TODO: Improve error handling
(defn wrap-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           {:status 500
            :body "Error occured"}))))

(def app
  (->
   (wrap-json-response app-routes)
   wrap-exception))

;; (app {:request-method :get, :uri "/patients"})
;; (app {:request-method :get, :uri "/patients/non-existings"})
;; (app {:request-method :get, :uri "/patients/b1393a03-8453-4f65-8b58-fd5631e66d66"})
