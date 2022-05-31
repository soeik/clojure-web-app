-- :name create-parients-table
-- :command :execute
-- :result :raw
-- :doc Create patients table
CREATE TABLE IF NOT EXISTS patients
(
id uuid NOT NULL DEFAULT gen_random_uuid(),
name character varying NOT NULL,
address character varying,
oms numeric,
PRIMARY KEY (id)
)


-- :name all-patients
-- :doc Get all patients
SELECT *
FROM patients

-- :name patient-by-id
-- :doc Get a single patient by id
SELECT *
FROM patients
WHERE id::text = :id

-- :name insert-patient :<!
-- :doc Insert a single patient
insert into patients (name)
values (:name)
returning id

-- :name update-patient :! :n
-- :doc Update a patient
update patients
set name = :name, address = :address, oms = :oms
where id::text = :id
