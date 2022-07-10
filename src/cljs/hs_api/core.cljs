(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   ["react-router-dom" :as router]
   [hs-api.components :as c]
   [hs-api.views :as v]
   [hs-api.styles :as styles]))

(defnc app []
  ($ router/BrowserRouter
     (d/div
      ($ c/app-header)
      (d/div {:class-name (styles/page-wrapper)}
             ($ router/Routes
                ($ router/Route {:path "/"
                                 :element ($ router/Navigate {:to "/patients"})})
                ($ router/Route {:path    "/patients"
                                 :element ($ v/patients-page)})
                ($ router/Route {:path    "/patients/new"
                                 :element ($ v/new-patient-page)})
                ($ router/Route {:path    "/patients/:id"
                                 :element ($ v/edit-patient-page)}))))))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))
