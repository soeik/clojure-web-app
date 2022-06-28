(ns hs-api.core
  (:require
   [helix.core :refer [defnc $]]
   [helix.dom :as d]
   ["react-dom" :as rdom]
   ["react-router-dom" :as router]
   [hs-api.components :as c]
   [hs-api.views :as v]
   [hs-api.styles :as styles]))

;; TODO: Switch to BrowserRouter

(defnc app []
  ($ router/HashRouter
     (d/div
      ($ c/app-header)
      (d/div {:class-name (styles/page-wrapper)}
             ($ router/Routes
                ($ router/Route {:path "/"
                                 :element ($ router/Navigate {:to "/patients"})})
                ($ router/Route {:path    "/patients"
                                 :element ($ v/list-patients)})
                ($ router/Route {:path    "/patients/new"
                                 :element ($ v/new-patient)})
                ($ router/Route {:path    "/patients/:id"
                                 :element ($ v/edit-patient)}))))))

(defn render []
  (rdom/render ($ app) (js/document.getElementById "app")))

(defn ^:export init! [] (render))
