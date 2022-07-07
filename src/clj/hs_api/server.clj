(ns hs-api.server
  (:require
   [ring.adapter.jetty :refer [run-jetty]]
   [hs-api.handler :refer [app]]
   [hs-api.db :refer [init-db]])
  (:gen-class))

(defn -main [& args]
  (let [port 9000]
    (init-db)
    (run-jetty #'app {:port port :join? false})))
