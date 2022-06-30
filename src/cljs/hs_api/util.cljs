(ns hs-api.util
  (:require [helix.hooks :as hooks]))

(defn use-request [request-fn]
  (let [[in-progress set-in-progress] (hooks/use-state false)
        [error set-error] (hooks/use-state nil)
        [result set-result] (hooks/use-state nil)
        handle-success #(do (set-in-progress false)
                            (set-result %))
        handle-error #(do (set-in-progress false)
                          (set-error %))]
    [(fn [args]
       (do
         (set-error nil)
         (set-result nil)
         (set-in-progress true)
         (apply request-fn (conj [] args handle-success handle-error))))
     in-progress
     result
     error]))
