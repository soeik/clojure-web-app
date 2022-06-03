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

-- :name get-all-patients
-- :doc Get all patients
SELECT *
FROM patients

-- :name get-patient
-- :doc Get a single patient by id
SELECT *
FROM patients
WHERE id::text = :id

-- :name insert-patient :<!
-- :doc Insert a single patient
insert into patients (name, address, oms)
values (:name, :address, :oms)
returning id

-- :name update-patient :! :n
-- :doc Update a patient
update patients
set name = :name, address = :address, oms = :oms
where id::text = :id

-- :name delete-patient :! :n
-- :doc Delete a patient
delete from patients where id::text = :id
