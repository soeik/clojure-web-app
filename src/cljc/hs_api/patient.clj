(ns hs-api.patient
  (:require [clojure.spec.alpha :as s])
  (:require [hs-api.validation :refer :all]))

(s/def :entry/oms oms-valid?)
(s/def :entry/name string?)
(s/def :entry/address string?)
;; TODO: date-of-birth
;; TODO: gender

(s/def :entry/patient (s/keys
                       :req-un [:entry/name :entry/oms]
                       :opt-un [:entry/address]))

(defn patient-valid? [patient] (s/valid? :entry/patient  patient))

(comment
  (s/valid? :entry/patient {:foo "bar"})
  (s/valid? :entry/patient {:name "Frank Cowperwood"
                            :date-of-birth "1989-05-05"
                            :address "Berlinerstrasse 124, Berlin, Germany"
                            :oms "1234567890123456"
                            :gender "M"})
  (s/valid? :entry/patient {:oms "1234567890123456",
                            :name "Frank Cowperwood",
                            :address "Sundgauerstr. 123" })
  (s/valid? :entry/patient {:oms 1234567890123456,
                            :name "Frank Cowperwood",
                            :gender ""
                            :date-of-birth ""
                            :address "Sundgauerstr. 123" }))
