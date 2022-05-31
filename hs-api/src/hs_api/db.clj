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
(defn create-patient [patient]
  (insert-patient db patient))

;; TODO: streamline naming, maybe add namespace
;; Update patient
(defn update-patient-by-id [id patient]
  (update-patient db (assoc patient :id id)))

;; Get all patients
(defn get-all-patients []
  (all-patients db))

;; Get a single patient by id
(defn get-patient-by-id [id]
  (patient-by-id db {:id id}))

(comment
  (create-patient {:name "Frank Cowperwood"})
  (all-patients db)
  (get-patient-by-id "b1393a03-8453-4f65-8b58-fd5631e66d66")
  (update-patient-by-id {:id     "b1393a03-8453-4f65-8b58-fd5631e66d66",
                         :name    "Donald Trumpet",
                         :address "Philadelphia ave."
                         :oms     5646576767 })
  (get-patient-by-id "unkown"))
