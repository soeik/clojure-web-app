(defproject hs-api "0.1.0-SNAPSHOT"
  :description "Web app in Clojure"
  :url ""
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.3.218"]
                 [compojure "1.6.1"]
                 [metosin/ring-http-response "0.9.3"]
                 [ring/ring-json "0.5.1"]
                 [com.layerware/hugsql "0.5.3"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.3.1"]]

  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler hs-api.handler/app
         :port 9000}
  :main hs-api.server

  :source-paths ["src/clj" "src/cljc" "src/cljs"]

  :Profilesp
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
