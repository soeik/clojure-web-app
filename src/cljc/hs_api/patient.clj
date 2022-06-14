(ns hs-api.patient
  (:require [clojure.spec.alpha :as s])
  (:require [hs-api.validation :refer :all]))

(s/def :entry/oms oms-valid?)
(s/def :entry/name string?)
(s/def :entry/address string?)


(s/def :entry/patient (s/keys
                       :req-un [:entry/name :entry/oms]
                       :opt-un [:entry/address]))

(defn patient-valid? [patient] (s/valid? :entry/patient  patient))

(comment
  (s/valid? :entry/patient {:foo "bar"})
  (s/valid? :entry/patient {:oms 1234567890123456,
                            :name "Frank Cowperwood",
                            :address "Sundgauerstr. 123" }))
