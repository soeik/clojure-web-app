(ns hs-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.data.json :refer [wrap-json-request wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.page :refer [include-js include-css html5]]
            [hs-api.patient :refer [patient-valid?]]
            [hs-api.db :refer [db-client]]))

(defn index-page []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (include-css "/css/normalize.css")
    (include-css "/css/skeleton.css")
    (include-css "/css/styles.css")]
   [:body
    [:div#app]
    (include-js "/js/app.js")]))

(defn index-handler
  [_req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (index-page)})

(defn entry->dto [col]
  (as-> col v
      (update v :date-of-birth str)
      (update v :id str)))

(defroutes api-routes
  (context "/api/patients" []
           (GET "/" {:keys [db query-params]}
                (let [ query (get query-params "query")
                      gender (get query-params "gender")
                      date-of-birth (get query-params "date-of-birth")
                      sort-column (get query-params "sort-column")
                      sort-order (get query-params "sort-order")
                      results ((db :search-patients) query gender date-of-birth sort-column sort-order)]
                  {:status 200 :body (map entry->dto results)}))
           (POST "/" {:keys [db body]} (if (patient-valid? body)
                                         ;; TODO entry-dto adds date-of-birth
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
                              (if (= affected-rows 1)
                                {:status 200 :body {:id id :deleted true}}
                                {:status 400 :body "Failed to delete patient"}))))))

(defroutes site-routes
  (GET "/" [] index-handler)
  (GET "/patients" [] index-handler)
  (GET "/patients/:id" [] index-handler)
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-db [f db]
  (fn [req]
    (f (assoc req :db db))))

;; TODO Date in this century

(defn create-app [db] (wrap-db (routes api-routes site-routes) db))

(def app (->
          (create-app db-client)
          (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
          (wrap-json-request :key-fn keyword)
          wrap-json-response))
