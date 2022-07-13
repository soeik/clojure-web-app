(ns app.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::modal-visible
 (fn [db]
   (:modal-visible db)))

(re-frame/reg-sub
 ::in-progress
 (fn [db]
   (:in-progress db)))

(re-frame/reg-sub
 ::api-errors
 (fn [db]
   (:api-errors db)))

(re-frame/reg-sub
 ::patients
 (fn [db]
   (:patients db)))

(re-frame/reg-sub
 ::patient
 (fn [db]
   (:patient db)))
