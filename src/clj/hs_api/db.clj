(ns hs-api.db
  (:require [hs-api.queries :as query])
  (:require [clojure.java.jdbc :as j]))

(def db {:dbtype "postgresql"
         :dbname "hs-db"
         :host (or (System/getenv "DB_HOST") "localhost")
         :user (or (System/getenv "DB_USER") "postgres")
         :password (System/getenv "DB_PASSWORD")
         :stringtype "unspecified"})

;; Init db
(query/init-db db)

;; Create a signle patient
(defn create-patient [patient]
  (query/insert-patient db patient))

;; Update patient
(defn update-patient [id patient]
  (query/update-patient db (assoc patient :id id)))

;; Get all patients
(defn get-all-patients []
  (query/get-all-patients db))

;; Get a single patient by id
(defn get-patient [id]
  (query/get-patient db {:id id}))

;; Delete a single patient by id
(defn delete-patient [id]
  (query/delete-patient db {:id id}))


(comment
  (create-patient {:name "Frank Cowperwood"
                   :date-of-birth "1989-05-05"
                   :address "Berlinerstrasse 124, Berlin, Germany"
                   :oms "1234567890123456"
                   :gender "M"})
  (get-all-patients)
  (get-patient "b1393a03-8453-4f65-8b58-fd5631e66d66")
  (update-patient {:id "b1393a03-8453-4f65-8b58-fd5631e66d66",
                   :name "Donald Trumpet",
                   :address "Philadelphia ave."
                   :oms 5646576767 })
  (get-patient "unkown"))
