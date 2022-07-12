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

(defn init-db []
  (do (query/create-gender-type db)
      (query/create-patients-table db)))

(defn cleanup-db []
  (query/cleanup-patients-table db))

(defn create-patient [patient]
  (query/insert-patient db patient))

(defn update-patient [id patient]
  (query/update-patient db (assoc patient :id id)))

(defn search-patients [search-query gender date-of-birth sort-column sort-order]
  (query/search-patients db {:search-query (if-not (empty? search-query)
                                             (str "%" (lower-case search-query) "%")
                                             nil)
                             :gender gender
                             :date-of-birth date-of-birth
                             :sort-column (sort-columns (keyword sort-column) "name")
                             :sort-order (sort-orders (keyword sort-order) "asc")}))

(defn get-patient [id]
  (query/get-patient db {:id id}))

(defn delete-patient [id]
  (query/delete-patient db {:id id}))
