(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defn get-patient [id]
  (response {:id id}))

(defroutes app-routes
  (GET "/" [] (response {:foo "bar"}))
  (GET "/patients/:id" [id] (get-patient id))
  (route/not-found "Not Found"))

(def app
  (wrap-json-response app-routes))
