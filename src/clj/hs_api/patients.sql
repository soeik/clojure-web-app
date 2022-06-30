-- :name create-gender-type
-- :command :execute
-- :result :raw
-- :doc Create gender type
DO $$
BEGIN
IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'gender') THEN
CREATE TYPE gender AS ENUM ('M', 'F');
END IF;
END$$;

-- :name create-patients-table
-- :command :execute
-- :result :raw
-- :doc Create patients table
CREATE TABLE IF NOT EXISTS patients
(
id uuid NOT NULL DEFAULT gen_random_uuid(),
name character varying NOT NULL,
gender gender,
address character varying,
oms character (16),
date_of_birth date,
PRIMARY KEY (id)
)

-- :name get-all-patients
-- :doc Get all patients
SELECT id, name, address, oms, gender, date_of_birth "date-of-birth"
FROM patients

-- :name search-patients
-- :doc Search patients by name or oms
SELECT id, name, address, oms, gender, date_of_birth "date-of-birth"
FROM patients
WHERE name LIKE :search-query OR oms LIKE :search-query

-- :name get-patient
-- :result :one
-- :doc Get a single patient by id
SELECT name, address, oms, gender, date_of_birth "date-of-birth"
FROM patients
WHERE id::text = :id

-- :name insert-patient :<!
-- :result :one
-- :doc Insert a single patient
insert into patients (name, address, oms, gender, date_of_birth)
values (:name, :address, :oms, :gender, :date-of-birth)
returning id

-- :name update-patient :! :n
-- :result :one
-- :doc Update a patient
update patients
set name = :name, address = :address, oms = :oms, gender = :gender, date_of_birth = :date-of-birth
where id::text = :id

-- :name delete-patient :! :n
-- :result :one
-- :doc Delete a patient
delete from patients where id::text = :id
