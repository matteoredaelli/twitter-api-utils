(ns twitter-api-utils.core
  (:require ;; my
            [cheshire.core :refer :all])
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [twitter.utils]
   [twitter.request]
   [twitter.api.restful]
   [twitter.api.search]
   [environ.core]
   )
)

(def oauth-creds (make-oauth-creds
                  (env :twitter-app-consumer-key)
                  (env :twitter-app-consumer-secret)
                  (env :twitter-user-access-token)
                  (env :twitter-user-access-token-secret)
                  ))

(defn oldest-id [tweets]
  (apply min (map :id tweets)))

(defn fetch-user-timeline-single 
  [params]
  (let [results (statuses-user-timeline
                 :oauth-creds oauth-creds
                 :params (merge params {:count 100}))
        tweets (:body results)
        total-now (count tweets)]
    (println "total tweets fetched: " total-now)
    tweets))

(defn fetch-user-timeline 
  [buf params total]
  (let [tweets (fetch-user-timeline-single params)
        total-now (count tweets)
        new-buf (concat tweets buf)
        new-total (- total total-now)]    
    ;; keep searching for older tweets until we have at least 1500
    (if (and (> new-total 0) (>= total-now 100))
      (recur new-buf params new-total (oldest-id tweets))
      new-buf))

  [buf params total max-id]
  (let [other-params (merge params {:max_id max-id})
        tweets (fetch-user-timeline-single other-params)
        total-now (count tweets)
        new-buf (concat tweets buf)
        new-total (- total total-now)]    
    ;; keep searching for older tweets until we have at least 1500
    (if (and (> new-total 0) (>= total-now 100))
      (recur new-buf params new-total (oldest-id tweets))
      new-buf))
)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
