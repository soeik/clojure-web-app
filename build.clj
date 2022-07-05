(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file "target/hs-app-standalone.jar")

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (prinln "Start build")
  (clean nil)
  (prinln "Copy files")
  (b/copy-dir {:src-dirs ["src/clj" "src/cljc" "src/cljs" "resources"]
               :target-dir class-dir})
  (prinln "Compile clj")
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj" "src/cljc" "src/cljs"]
                  :class-dir class-dir})
  (prinln "Uberjar")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'hs-api.server}))
