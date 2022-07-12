(ns hs-api.handler-test
  (:require [clojure.test :refer [deftest testing is]]
            [matcho.core :refer :all :as m]
            [hs-api.data :as db]
            [hs-api.handler :refer [api-routes]]))

(def user-mock {:name "Test User"
                :gender "M"
                :date-of-birth "1990-01-01"
                :address "Test Address"
                :oms "1234567890123456"})

(defn get-patients [query-params]
  (api-routes {:request-method :get
                :uri "/api/patients"
                :query-params query-params}))

(defn create-patient [body]
  (api-routes {:request-method :post
               :uri "/api/patients"
               :body body}))

(deftest handler-test
  (testing "api routes"
    (testing "GET /api/patients"
      (db/reset nil)
      (testing "filtering"
        (m/assert
         {:status 200 :body #(= (count %) 3)}
         (get-patients nil))
        (m/assert
         {:status 200 :body #(= (count %) 1)}
         (get-patients {"date-of-birth" "1990-01-01"}))
        (m/assert
         {:status 200 :body #(= (count %) 1)}
         (get-patients {"query" "321"}))
        (m/assert
         {:status 200 :body #(= (count %) 1)}
         (get-patients {"query" "Test User 1"}))
        (m/assert
         {:status 200 :body #(= (count %) 2)}
         (get-patients {"gender" "F"}))
        (m/assert
         {:status 200 :body #(= (count %) 1)}
         (get-patients  {"gender" "M"})))
      (testing "sorting"
        (m/assert
         {:status 200 :body #(= (:name (first %)) "Test User 1")}
         (get-patients nil))
        (m/assert
         {:status 200 :body #(= (:name (first %)) "Test User 3")}
         (get-patients {"sort-order" "desc"}))
        (m/assert
         {:status 200 :body #(= (:name (first %)) "Test User 2")}
         (get-patients {"sort-column" "gender"
                        "sort-order" "desc"}))
        (m/assert
         {:status 200 :body #(= (:name (first %)) "Test User 3")}
         (get-patients {"sort-column" "date-of-birth"
                        "sort-order" "desc"}))))
    (testing "POST /api/patients"
      (db/cleanup nil)
      (m/assert
       {:status 400 :body not-empty}
       (create-patient {}))
      (m/assert
       {:status 201 :body not-empty}
       (create-patient user-mock))
      (m/assert
       #(= (count (:body %)) 1)
       (get-patients nil))
      (m/assert
       {:body #(= (dissoc (first %) :id) user-mock)}
       (get-patients nil)))))

;; TODO: PUT
;; TODO: Not Found
