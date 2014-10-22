(ns twitter-api-utils.core
  (:require ;; my
   [cheshire.core :refer :all]
   [clj-http.client :as http.client]
   )
  (:use 
   [twitter.oauth]
   [twitter.api.restful]
   [twitter.utils]
   [twitter.request]
   [twitter.api.restful]
   [twitter.api.search]
   [environ.core]
   [clojurewerkz.urly.core]
   [selmer.parser])) 

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


(defn extract-users-from-tweets
  [tweets]
  (distinct (map :user tweets)))

(defn extract-created-at-datetime-from-tweets
  [tweets]
  (map #(.parse (java.text.SimpleDateFormat. "EEE MMM dd HH:mm:ss zzz yyyy")  %)
       (map :created_at tweets)))


(defn extract-entities-hashtags-from-tweets
  [tweets]
  ;; (sort-by val (frequencies (map clojure.string/lower-case (flatten (map #(map :text (:hashtags (:entities %))) a)))))
  (map clojure.string/lower-case (flatten (map #(map :text (:hashtags (:entities %))) tweets))))

(defn extract-entities-urls-from-tweets
  [tweets]
  (map clojure.string/lower-case (flatten (map #(map :expanded_url (:urls (:entities %))) tweets))))

(defn extract-domains-from-urls
  [urls]
  (map host-of urls))

(defn extract-entities-user_mentions-from-tweets
  [tweets]
  (map clojure.string/lower-case (flatten (map #(map :screen_name (:user_mentions (:entities %))) tweets))))

(defn top-tweets-with-favorites [n tweets]
  (->> tweets
       (sort-by :favorite_count)
       reverse
       (take n)
       ))

(defn top-tweets-with-retweets [n tweets]
  (->> tweets
       (sort-by :retweet_count)
       reverse
       (take n)
       ))

(defn most-frequent-n-with-counts [n items]
  (->> items
    frequencies
    (sort-by val)
    reverse
    (take n)))

(defn most-frequent-n [n items]
  (map first (most-frequent-n-with-counts n items)))

(defn get-url-title [url]
  (let [headers (:headers (clj-http.client/head url))
        content-type (headers "Content-Type")
        is-html (count (re-seq #"text/html" content-type))]
    (if is-html
      (let [body (:body (clj-http.client/get url))
            title (trim (nth (re-find #"<title>(.*)</title>" body) 1))]
        title)
      content-type)))
 
(defn split-text-to-words [text]
  (re-seq #"\w+" text))

(defn report-timeline-html [tweets]
  (render-file "timeline.html" 
               {:title "Yogthos" 
                :users (distinct (map :user tweets))}))
 

;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
