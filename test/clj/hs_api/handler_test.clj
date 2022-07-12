(ns hs-api.handler-test
  (:require [clojure.test :refer :all]))

#_(deftest test-app
  (let [app (create-app mock-db-client)]
    (testing "not-found route"
      (let [response (app {:request-method :get
                           :uri "/invalid"})]
        (is (= (:status response) 404))))

    (testing "GET /patients"
      (let [response (app {:request-method :get
                           :uri "/api/patients"})]
        (is (= (:status response) 200))
        (is (= (count (:body response)) 1))
        (is (= (:body response) [mock-patient]))))

    (testing "GET /patients/:id"
      (testing "returns a patient"
        (let [response (app {:request-method :get
                             :uri  "/api/patients/u-1"})]
          (is (= (:status response) 200))
          (is (= (:body response) mock-patient))))

      (testing "returns status 404 if patient doesn't exist"
        (let [response (app {:request-method :get
                             :uri "/api/patients/u-2"})]
          (is (= (:status response) 404)))))

    (testing "DELETE /patients/:id"
      (testing "returns ok if patient deleted"
        (let [response (app {:request-method :delete
                             :uri "/api/patients/u-1"})]
          (is (= (:status response) 200))))

      (testing "returns status 400 if deletion failed"
        (let [response (app {:request-method :delete
                             :uri "/api/patients/u-2"})]
          (is (= (:status response) 400)))))

    (testing "POST /patients"
      (testing "returns status 400 when input is invalid"
        (let [response (app {:request-method :post
                             :uri "/api/patients/"
                             :body {:foo "bar"}})]
          (is (= (:status response) 400))))

      (testing "returns an id if request succeeded"
        (let [response (app {:request-method :post
                             :uri "/api/patients/"
                             :body (dissoc mock-patient :id)})]
          (is (= (:status response) 201))
          (is (= (:id (:body response)) "u-1")))))

    (testing "PUT /patients/:id"
      (testing "updates patient"
        (let [response (app {:request-method :put
                             :uri "/api/patients/u-1"
                             :body (dissoc mock-patient :id)})]
          (is (= (:status response) 200))))

      (testing "returns  Invalid input if patient is invalid"
        (let [response (app {:request-method :put
                             :uri "/api/patients/u-1"
                             :body {}})]
          (is (= (:status response) 400))
          (is (= (:body response) "Invalid input"))))

      (testing "returns Failed to update patient if patient not found"
        (let [response (app {:request-method :put
                             :uri "/api/patients/u-2"
                             :body mock-patient})]
          (is (= (:status response) 400))
          (is (= (:body response) "Failed to update patient")))))))
