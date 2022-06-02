(ns hs-api.validation-test
  (:require [clojure.test :refer :all]
            [hs-api.validation :refer :all]))

(deftest test-oms-validation
  (testing "validates oms number correctly"
    (is (= (oms-valid?) false))
    (is (= (oms-valid? "123") false))
    (is (= (oms-valid? 123) false))
    (is (= (oms-valid? 1234567890123456) true))))
