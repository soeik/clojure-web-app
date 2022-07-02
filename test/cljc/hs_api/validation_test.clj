(ns hs-api.validation-test
  (:require [clojure.test :refer :all]
            [hs-api.validation :refer :all]))

(deftest test-oms-valid?
  (testing "validates oms number correctly"
    (is (= (oms-valid?) false))
    (is (= (oms-valid? "123") false))
    (is (= (oms-valid? "1234567890123456") true))))

(deftest test-date-of-birth-valid?
  (testing "validates date input")
  (is (= (date-of-birth-valid? "") false))
  (is (= (date-of-birth-valid? "12345") false))
  (is (= (date-of-birth-valid? nil) false))
  (is (= (date-of-birth-valid? "2000-50-20") false))
  (is (= (date-of-birth-valid? "1970-01-01") true)))

(deftest test-date-of-birth-in-past?
  (testing "checks if the date is in the past")
  (is (= (date-of-birth-in-past? "3000-05-05") false))
  (is (= (date-of-birth-in-past? "1970-01-01") true)))
