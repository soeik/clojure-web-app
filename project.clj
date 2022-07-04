(defproject hs-api "0.1.0-SNAPSHOT"
  :description "Web app in Clojure"
  :url ""
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.51"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/spec.alpha "0.3.218"]
                 [org.slf4j/slf4j-simple "1.7.5"]
                 [chbrown/ring-data.json "0.1.0"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring/ring-defaults "0.3.3"]
                 [compojure "1.6.1"]
                 [com.layerware/hugsql "0.5.3"]
                 [org.postgresql/postgresql "42.3.1"]
                 [hiccup "2.0.0-alpha2"]
                 [com.widdindustries/cljc.java-time "0.1.21"]
                 [lilactown/helix "0.1.6"]
                 [cljs-ajax "0.7.5"]
                 [clj-commons/cljss "1.6.4"]]

  :plugins [[lein-ring "0.12.5"]
            [Lein-cljsbuild "1.1.8"]]

  :ring {:handler hs-api.handler/app
         :port 9000}

  :jar-name "hs-api.jar"
  :uberjar-name "hs-api-standalone.jar"
  :main hs-api.server
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :test-paths ["test/clj" "test/cljc" "test/cljs"]
  :resource-paths ["target" "resources"]
  :target-path "target/%s"
  :compile-path "%s/class-files"
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :jvm-opts ["-Xms500m" "-Xmx4g"]

  :profiles
  {:dev {:plugins []
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [thheller/shadow-cljs "2.19.1"]]}

   :cljs {:dependencies [[com.google.javascript/closure-compiler-unshaded "v20220502"]]}

   :uberjar {:source-paths ["env/prod/clj"]
             :env {:production true}
             :aot :all
             :omit-source true}})
