(ns hs-api.patient
  (:require [clojure.spec.alpha :as s])
  (:require [hs-api.validation :as validation]))

(s/def :form/name (s/and not-empty string?))
(s/def :form/gender (s/and string? (partial re-matches #"^(?:M|F)$")))

(s/def :form/oms-not-empty not-empty)
(s/def :form/oms-length validation/oms-valid?)
(s/def :form/oms (s/and string? :form/oms-not-empty :form/oms-length))

(s/def :form/address string?)
;; TODO: date-of-birth

(s/def :form/patient (s/keys
                       :req-un [:form/name :form/gender :form/oms]
                       :opt-un [:form/address]))

(def messages
  {:form/oms-length "OMS should contain 16 digits"
   :form/gender "Must be a valid gender"
   :form/oms-not-empty "OMS is required"
   :form/name "Name is required"})

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
