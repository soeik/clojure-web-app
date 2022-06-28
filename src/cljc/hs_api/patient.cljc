(ns hs-api.patient
  (:require [clojure.spec.alpha :as s])
  (:require [hs-api.validation :as validation]))

(s/def :form/oms-not-empty not-empty)
(s/def :form/oms-length validation/oms-valid?)
(s/def :form/oms (s/and string? :form/oms-not-empty :form/oms-length))

(s/def :form/name (s/and not-empty string?))
(s/def :form/address string?)
;; TODO: date-of-birth
;; TODO: gender

(s/def :form/patient (s/keys
                       :req-un [:form/name :form/oms]
                       :opt-un [:form/address]))

(def messages
  {:form/oms-length "OMS should contain 16 digits"
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
  (def result
    [{:oms :form/oms-length} {:name :form/name}])
  (:form/oms-length messages)
  (def problems [{:path [:oms],
                  :val "123",
                  :via
                  [:hs-api.patient/patient
                   :hs-api.patient/oms
                   :hs-api.patient/oms-length],
                  :in [:oms]}
                 {:path [:name],
                  :val "",
                  :via [:hs-api.patient/patient :hs-api.patient/name],
                  :in [:name]}])
  (map #(assoc {} (->> (:in %) first) (last (:via %))) problems)
  (s/valid? :form/patient {:foo "bar"})
  (s/explain-data :form/patient {:oms "" :name ""})
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
                     :gender "M"})
  (s/valid? :form/patient {:oms "1234567890123456",
                            :name "Frank Cowperwood",
                            :address "Sundgauerstr. 123" })
  (s/valid? :form/patient {:oms 1234567890123456,
                            :name "Frank Cowperwood",
                            :gender ""
                            :date-of-birth ""
                            :address "Sundgauerstr. 123" }))
