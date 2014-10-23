(ns twitter-api-utils.tweets
  (:require ;; my
   [cheshire.core :refer :all]
   )
  (:use 
   [selmer.parser])) 

(defn oldest-id [tweets]
  (apply min (map :id tweets)))

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

(defn extract-entities-user_mentions-from-tweets
  [tweets]
  (map clojure.string/lower-case (flatten (map #(map :screen_name (:user_mentions (:entities %))) tweets))))

(defn top-tweets-with-favorites [tweets n]
  (->> tweets
       (sort-by :favorite_count)
       reverse
       (take n)
       ))

(defn top-tweets-with-retweets [tweets n]
  (->> tweets
       (sort-by :retweet_count)
       reverse
       (take n)
       ))

(defn report-timeline-html [tweets title n]
  (let [t1 (top-tweets-with-retweets tweets n)
        t2 (top-tweets-with-favorites tweets n)
        t (distinct (concat t1 t2))
        urls (distinct (extract-entities-urls-from-tweets t))
  (render-file "timeline.html" 
               {:title title 
                :users (distinct (map :user t))
                :tweets t
                :urls urls
                
                }))
 

;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
