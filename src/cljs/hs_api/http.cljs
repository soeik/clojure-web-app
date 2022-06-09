(ns hs-api.http
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def request-methods
  {:get http/get
   :post http/post
   :put http/put
   :delete http/delete})

(defn request [method endpoint cb]
  (go (let [response (<! ((request-methods method) endpoint))]
        (cb (:body response)))))
