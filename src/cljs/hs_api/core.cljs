(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   ["react-router-dom" :as router]
   [hs-api.components :as c]))

;; TODO: Switch to BrowserRouter
;; TODO: Move pages to a separate ns

(defnc app []
  (d/div
   {:class-name "container"}
   ($ c/app-header)
   ($ router/HashRouter
      ($ router/Routes
         ($ router/Route {:path    "/"
                          :element ($ c/patients-table)})
         ($ router/Route {:path    "/new"
                          :element ($ c/new-patient)})
         ($ router/Route {:path    "/edit"
                          :element ($ c/edit-patient)})))))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))
