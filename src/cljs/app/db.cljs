(ns app.db)

(def empty-patient
  {:name ""
   :gender ""
   :date-of-birth ""
   :address ""
   :oms ""})

(def default-filter
  {:query ""
   :gender ""
   :date-of-birth ""
   :sort-column "name"
   :sort-order "asc"})

(def default-db
  {:modal-visible false
   :patient empty-patient})
