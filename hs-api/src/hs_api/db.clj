(ns hs-api.db
  (:require [hugsql.core :as hugsql])
  (:require [clojure.java.jdbc :as j]))


(def db {:dbtype "postgresql"
            :dbname "hs-db"
            :host (or (System/getenv "DB_HOST") "localhost")
            :user (or (System/getenv "DB_USER") "postgres")
            :password (System/getenv "DB_PASSWORD")})

(hugsql/def-db-fns "hs_api/queries/patients.sql")

;; Create table patients if not exists
(create-parients-table db)

;; Create a signle patient
(defn create-patient [name]
  (insert-patient db {:name name}))

;; Get all patients
(defn get-all-patients []
  (all-patients db))

;; Get a single patient by id
(defn get-patient-by-id [id]
  (patient-by-id db {:id id}))

;; (all-patients db)
;; (get-patient-by-id "b1393a03-8453-4f65-8b58-fd5631e66d66")
;; (get-patient-by-id "unkown")
