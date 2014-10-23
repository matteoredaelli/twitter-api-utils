(ns twitter-api-utils.twitter
  ;;(:require ;; my
  ;; [cheshire.core :refer :all]
  ;; [clj-http.client]
  ;; )
  (:use 
   [twitter.oauth]
   [twitter.api.restful]
   [twitter.utils]
   [twitter.request]
   [twitter.api.restful]
   [twitter.api.search]
   [environ.core]

   [twitter-api-utils.tweets])) 

(def oauth-creds (make-oauth-creds
                  (env :twitter-app-consumer-key)
                  (env :twitter-app-consumer-secret)
                  (env :twitter-user-access-token)
                  (env :twitter-user-access-token-secret)
                  ))


(defn fetch-user-timeline-single 
  [params]
  (let [results (statuses-user-timeline
                 :oauth-creds oauth-creds
                 :proxy (env :http-proxy-clj

                             )
                 :params (merge params {:count 100}))
        tweets (:body results)
        total-now (count tweets)]
    (binding [*out* *err*]
      (println "total tweets fetched: " total-now))
    tweets))

(defn fetch-user-timeline 
  [params total max-id buf]
  (let [other-params (if (> max-id 0) (merge params {:max_id max-id}) params)
        tweets (fetch-user-timeline-single other-params)
        total-now (count tweets)
        new-buf (concat tweets buf)
        new-total (- total total-now)]    
    ;; keep searching for older tweets until we have at least 1500
    (if (and (> new-total 0) (> total-now 0))
      (recur params new-total (oldest-id tweets) new-buf)
      new-buf))
)
