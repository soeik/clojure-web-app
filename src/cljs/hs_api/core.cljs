(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   ["react-router-dom" :as router]
   [hs-api.components :as c]
   [hs-api.views :as v]))

;; TODO: Switch to BrowserRouter

(defnc app []
  (d/div
   {:class-name "container"}
   ($ c/app-header)
   ($ router/HashRouter
      ($ router/Routes
         ($ router/Route {:path "/"
                          :element ($ router/Navigate {:to "/patients"})})
         ($ router/Route {:path    "/patients"
                          :element ($ v/list-patients)})
         ($ router/Route {:path    "/patients/new"
                          :element ($ v/new-patient)})
         ($ router/Route {:path    "/patients/:id"
                          :element ($ v/edit-patient)})))))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))
