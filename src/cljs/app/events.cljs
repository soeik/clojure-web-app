(ns app.events
  (:require
   [ajax.core :refer [json-request-format json-response-format]]
   [clojure.string :as str]
   [day8.re-frame.http-fx]
   [re-frame.core :refer[reg-event-db reg-event-fx]]
   [app.db :as db]))

(defn endpoint [& params]
  "Concat any params to base url separated by /"
  (str/join "/" (concat ["/api"] params)))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;; -- GET Patients @ /api/articles --------------------------------------------
;; usage (dispatch [:get-patients {params}]);;
(reg-event-fx
 :get-patients
 (fn [{:keys [db]} [_ params]]  ;; params = { TODO }
   {:http-xhrio {:method          :get
                 :uri             (endpoint "patients")
                 ;; :params          params
                 ;; :headers         (auth-header db)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-patients-success]
                 :on-failure      [:api-request-error :get-patients]}
    :db          (assoc db :loading true)}))

(reg-event-db
 :get-patients-success
 (fn [db [_ patients]]
   (-> db
       (assoc :loading false)
       (assoc :patients patients))))

;; FIXME Test event
(reg-event-db
 :change-name
 (fn [db new-name]
   (assoc db :name new-name)))
