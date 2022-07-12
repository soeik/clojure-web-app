(ns hs-api.api-test
  (:require [clojure.test :refer :all]
            [hs-api.db :refer [db-client cleanup-db]]
            [hs-api.handler :refer [create-app]]))

(def app (create-app db-client))

(deftest test-api
  (cleanup-db)
  (testing "get list of patients"
    (let [response (app {:request-method :get
                         :uri "/api/patients"})]
      (is (some? response)))))
