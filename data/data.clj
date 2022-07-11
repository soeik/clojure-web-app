(ns data
  (:require [hs-api.db :refer :all]))

(def patients
  (list {:name "Test User 1" :address "Test address" :gender "M" :oms "1234567890123456" :date-of-birth "1990-01-01"}
        {:name "Test User 2" :address "Test address" :gender "F" :oms "3214567890123456" :date-of-birth "1991-01-01"}
        {:name "Test User 3" :address "Test address" :gender "F" :oms "1234567890123456" :date-of-birth "1992-01-01"}))

(defn seed [_]
  (println "Seeding the data")
  (doseq [patient patients]
    (create-patient patient)))

(defn cleanup [_]
  (println "Cleaning up the data")
  (cleanup-db))

(comment
  (seed nil)
  (cleanup nil))
