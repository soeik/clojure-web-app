(ns hs-api.db
  (:require [hs-api.queries :as query]
            [clojure.java.jdbc :as j]))

(def db {:dbtype "postgresql"
         :dbname "hs-db"
         :host (or (System/getenv "DB_HOST") "localhost")
         :user (or (System/getenv "DB_USER") "postgres")
         :password (System/getenv "DB_PASSWORD")
         :stringtype "unspecified"})

;; TODO
;; Init db
(query/init-db db)

;; Create a signle patient
(defn create-patient [patient]
  (query/insert-patient db patient))

;; Update patient
(defn update-patient [id patient]
  (query/update-patient db (assoc patient :id id)))

(defn search-patients [search-query gender date-of-birth]
  (query/search-patients db {:search-query (str "%" search-query "%")
                             :gender gender
                             :date-of-birth date-of-birth}))

;; Get a single patient by id
(defn get-patient [id]
  (query/get-patient db {:id id}))

;; Delete a single patient by id
(defn delete-patient [id]
  (query/delete-patient db {:id id}))

(def db-client {:search-patients search-patients
                :get-patient get-patient
                :delete-patient delete-patient
                :update-patient update-patient
                :create-patient create-patient})

(comment
  (search-patients "098" "F" nil)
  (query/search-patients-sqlvec db {:search-query "TEST"})
  (create-patient {:name "Frank Cowperwood"
                   :date-of-birth "1989-05-05"
                   :address "Berlinerstrasse 124, Berlin, Germany"
                   :oms "1234567890123456"
                   :gender "M"})
  (get-patient "b1393a03-8453-4f65-8b58-fd5631e66d66")
  (update-patient "86f6e38b-fa0a-40b8-bce5-903df0fb97d4"
                  {:id "b1393a03-8453-4f65-8b58-fd5631e66d66",
                   :name "Donald Trumpet",
                   :address "Philadelphia ave."
                   :gender "M"
                   :oms "5646576767"
                   :date-of-birth "2016-06-20"})
  (get-patient "unkown"))
