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
   [clojurewerkz.urly.core]
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
                 :proxy (env :http-proxy-clj

                             )
                 :params (merge params {:count 100}))
        tweets (:body results)
        total-now (count tweets)]
    (println "total tweets fetched: " total-now)
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

(defn extract-entities-hashtags-from-tweets
  [tweets]
  ;; (sort-by val (frequencies (map clojure.string/lower-case (flatten (map #(map :text (:hashtags (:entities %))) a)))))
  (map clojure.string/lower-case (flatten (map #(map :text (:hashtags (:entities %))) tweets))))

(defn extract-entities-urls-from-tweets
  [tweets]
  (map clojure.string/lower-case (flatten (map #(map :expanded_url (:urls (:entities %))) tweets))))

(defn extract-domains-from-urls
  [urls]
  (map path-of urls))

(defn extract-entities-user_mentions-from-tweets
  [tweets]
  (map clojure.string/lower-case (flatten (map #(map :screen_name (:user_mentions (:entities %))) tweets))))

(defn most-frequent-n-with-counts [n items]
  (->> items
    frequencies
    (sort-by val)
    reverse
    (take n)))

(defn most-frequent-n [n items]
  (map first (most-frequent-n-with-counts n items)))
