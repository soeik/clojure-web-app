(ns hs-api.db
  (:require [clojure.java.jdbc :as j]
            [clojure.string :refer [lower-case]]
            [hs-api.queries :as query]))

(def sort-columns
  {:name "name"
   :gender "gender"
   :date-of-birth "date_of_birth"})

(def sort-orders
  {:asc "asc"
   :desc "desc"})

(def db {:dbtype "postgresql"
         :dbname "hs-db"
         :host (or (System/getenv "DB_HOST") "localhost")
         :user (or (System/getenv "DB_USER") "postgres")
         :password (System/getenv "DB_PASSWORD")
         :stringtype "unspecified"})

;; Init db
(defn init-db []
  (do (query/create-gender-type db)
      (query/create-patients-table db)))

;; Cleanup db
(defn cleanup-db []
  (query/cleanup-patients-table db))

;; Create a signle patient
(defn create-patient [patient]
  (query/insert-patient db patient))

;; Update patient
(defn update-patient [id patient]
  (query/update-patient db (assoc patient :id id)))

;; Search patients by name, oms, gender, date of birth
(defn search-patients [search-query gender date-of-birth sort-column sort-order]
  (query/search-patients db {:search-query (if-not (empty? search-query)
                                             (str "%" (lower-case search-query) "%")
                                             nil)
                             :gender gender
                             :date-of-birth date-of-birth
                             :sort-column (sort-columns (keyword sort-column) "name")
                             :sort-order (sort-orders (keyword sort-order) "asc")}))

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
  (search-patients nil nil nil "name" "desc")
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
