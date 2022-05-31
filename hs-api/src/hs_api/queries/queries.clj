(ns hs-api.db.queries
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "hs_api/queries/patients.sql")
