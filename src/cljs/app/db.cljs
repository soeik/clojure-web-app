(ns app.db)

(def empty-patient
  {:name ""
   :gender ""
   :date-of-birth ""
   :address ""
   :oms ""})

(def default-db
  {:modal-visible false
   :patient empty-patient})
