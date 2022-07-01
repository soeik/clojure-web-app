(ns hs-api.patient
  (:require [clojure.spec.alpha :as s])
  (:require [hs-api.validation :as validation]))

(s/def :form/name (s/and not-empty string?))
(s/def :form/gender (s/and string? (partial re-matches #"^(?:M|F)$")))

(s/def :form/oms-not-empty not-empty)
(s/def :form/oms-length validation/oms-valid?)
(s/def :form/oms (s/and string? :form/oms-not-empty :form/oms-length))

(s/def :form/date-of-birth-not-empty not-empty)
(s/def :form/date-of-birth-valid validation/date-of-birth-valid?)
(s/def :form/date-of-birth-in-past validation/date-of-birth-in-past?)
(s/def :form/date-of-birth (s/and
                            :form/date-of-birth-not-empty
                            :form/date-of-birth-valid
                            :form/date-of-birth-in-past))

(s/def :form/address string?)

(s/def :form/patient (s/keys
                       :req-un [:form/name :form/gender :form/oms :form/date-of-birth]
                       :opt-un [:form/address]))

(def messages
  {:form/oms-length "OMS should contain 16 digits"
   :form/gender "Must be a valid gender"
   :form/oms-not-empty "OMS is required"
   :form/name "Name is required"
   :form/date-of-birth-valid "Date of birth must be a valid date"
   :form/date-of-birth-in-past "Date of birth must be in the past"
   :form/date-of-birth-not-empty "Date of birth is required"})

(defn ?spec-problems
  [spec value]
  (-> (s/explain-data spec value)
      :cljs.spec.alpha/problems))

(defn validate-patient
  [value]
  (->> (?spec-problems :form/patient value)
       (map #(assoc {}
                    (->> (:in %) first)
                    (messages (last (:via %)))))
       (reduce into {})))

(defn patient-valid? [patient] (s/valid? :form/patient patient))

(comment
  "2022-07-14"
  (s/explain-data :form/patient {:oms "" :name "" :gender "F"})
  (?spec-problems :form/patient {:oms "" :name ""})
  (validate-patient {:oms "" :name ""})
  (validate-patient {:name "Frank Cowperwood"
                     :date-of-birth "1989-05-05"
                     :address "Berlinerstrasse 124, Berlin, Germany"
                     :oms "1234567890123456"
                     :gender "M"})
  (patient-valid? {:name "Frank Cowperwood"
                   :date-of-birth "1989-05-05"
                   :address "Berlinerstrasse 124, Berlin, Germany"
                   :oms "1234567890123456"
                     :gender "M"}))
