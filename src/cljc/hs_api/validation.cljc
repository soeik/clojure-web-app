(ns hs-api.validation
  (:require [cljc.java-time.local-date :as ld]
            [cljc.java-time.format.date-time-formatter :as formatter]))

(defn oms-valid?
  ([] false)
  ([oms] (and
          (string? oms)
          (= (count oms) 16))))

(def date-format (formatter/of-pattern "yyyy-MM-dd"))

(defn date-of-birth-valid?
  [date]
  (try (ld/parse date date-format) true
       (catch #?(:clj Exception :cljs js/Error) e false)))

(defn date-of-birth-in-past?
  [date]
  (try (.isBefore
        (ld/parse date date-format)
        (ld/now))
       (catch #?(:clj Exception :cljs js/Error) e false)))


(comment
  (.isBefore
   (ld/parse "2032-05-05" date-format)
   (ld/parse "3050-05-05" date-format)
   (ld/now))
  (date-of-birth-valid? "2022-05-05")
  )
