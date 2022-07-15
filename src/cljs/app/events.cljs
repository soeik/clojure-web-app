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

(defn remove-empty-values [filter]
  (apply dissoc filter (for [[k v] filter :when (empty? v)] k)))

(reg-event-fx
 ::initialize-db
 (fn [_ _]
   {:db db/default-db
    :dispatch [:get-patients]}))

(reg-event-db
 :set-modal-visible
 (fn [db [_ visible]]
   (-> db
       (assoc :modal-visible visible)
       (assoc-in [:api-request-errors :submit-patient] nil))))

(reg-event-db
 :api-request-error
 (fn [db [_ key response]]
   (-> db
       (assoc-in [:in-progress key] false)
       (assoc-in [:api-request-errors key] response))))

;; Get patients
(reg-event-fx
 :get-patients
 (fn [{:keys [db]} [_ _]]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "patients")
                 :params          (remove-empty-values (:filter db))
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-patients-success]
                 :on-failure      [:api-request-error]}
    :db          (assoc-in db [:in-progress :get-patients] true)}))

(reg-event-db
 :get-patients-success
 (fn [db [_ patients]]
   (-> db
       (assoc-in [:in-progress :get-patients] false)
       (assoc :patients patients))))

(reg-event-fx
 :set-filter
 (fn [{:keys [db]} [_ [k v]]]
   {:db (assoc-in db [:filter k] v)
    :dispatch [:get-patients]}))

(reg-event-fx
 :reset-filter
 (fn [{:keys [db]} [_ _]]
   {:db (assoc db :filter db/default-filter)
    :dispatch [:get-patients]}))

(reg-event-db
 :set-patient-form
 (fn [db [_ [k v]]]
   (assoc-in db [:patient k] v)))

(reg-event-fx
 :submit-patient
 (fn [{:keys [db]} _]
   {:dispatch
    (if (nil? (:patient-id db))
      [:create-patient]
      [:update-patient])}))

;; Create patient
(reg-event-db
 :new-patient
 (fn [db [_ _]]
   (-> db
       (assoc :modal-visible true)
       (assoc :patient db/empty-patient)
       (assoc :patient-id nil))))

(reg-event-fx
 :create-patient
 (fn [{:keys [db]} [_ _]]
   {:http-xhrio {:method          :post
                 :uri             (endpoint "patients")
                 :params          (:patient db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:create-patient-success]
                 :on-failure      [:api-request-error :submit-patient]}
    :db          (assoc-in db [:in-progress :submit-patient] true)}))

(reg-event-fx
 :create-patient-success
 (fn [{:keys [db]} [_ _]]
   {:db (assoc-in db [:in-progress :create-patient] false)
    :fx [[:dispatch [:set-modal-visible false]]
         [:dispatch [:get-patients nil]]]}))

;; Edit patien
(reg-event-fx
 :edit-patient
 (fn [{:keys [db]} [_ id]]
   {:db
    (-> db
        (assoc :modal-visible true)
        (assoc :patient-id id))
    :dispatch [:get-single-patient id]}))

(reg-event-fx
 :get-single-patient
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :get
                 :uri             (endpoint "patients" id)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:get-single-patient-success]
                 :on-failure      [:api-request-error :get-single-patient]}
    :db          (assoc-in db [:in-progress :get-single-patient] true)}))

(reg-event-db
 :get-single-patient-success
 (fn [db [_ patient]]
   (-> db
       (assoc-in [:in-progress :get-single-patient] false)
       (assoc :patient patient))))

(reg-event-fx
 :update-patient
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :put
                 :uri             (endpoint "patients" (:patient-id db))
                 :params          (:patient db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:update-patient-success]
                 :on-failure      [:api-request-error :submit-patient]}
    :db          (assoc-in db [:in-progress :submit-patient] true)}))

(reg-event-fx
 :update-patient-success
 (fn [{:keys [db]} [_ _]]
   {:db (-> db
            (assoc-in [:api-request-errors :submit-patient] nil)
            (assoc-in [:in-progress :submit-patient] false))
    ;; TODO Notify user that patient is updated
    :dispatch [:get-patients nil]}))
