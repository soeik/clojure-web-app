(ns hs-api.server
  (:require
   [hs-api.handler :refer [app]]
   [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (let [port 9000]
    (run-jetty #'app {:port port :join? false})))
