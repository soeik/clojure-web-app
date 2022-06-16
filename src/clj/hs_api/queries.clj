(ns hs-api.queries
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "hs_api/patients.sql")

(defn init-db [db]
  (do (create-gender-type db)
      (create-patients-table db)))
