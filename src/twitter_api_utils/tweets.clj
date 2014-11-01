(ns twitter-api-utils.tweets
  (:require ;; my
   [cheshire.core :refer :all]
   )
  (:use 
   [twitter-api-utils.urls]
   [twitter-api-utils.text]
   [twitter-api-utils.utils]
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
  (map :screen_name (flatten (map #(get-in % [:entities :user_mentions]) tweets))))

(defn extract-cleaned-text-from-tweets
  [tweets]
  (let [text (clojure.string/join " " (map :text tweets))]
    (clean-text text
                ["remove-RTs" 
                 "remove-urls"
                 "tolower"
                 "remove-extra-whitespaces"])))

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

(defn report-timeline-html [tweets title n stopwords]
  (let [;;t1 (top-tweets-with-retweets tweets n)
        ;;t2 (top-tweets-with-favorites tweets n)
        ;;t (distinct (concat t1 t2))
        t tweets
        words (extract-words-from-text (extract-cleaned-text-from-tweets t))
        filtered_words (remove-stopwords-from-words words stopwords)
        hashtags (extract-entities-hashtags-from-tweets t)
        user_mentions (extract-entities-user_mentions-from-tweets t)
        urls (distinct (extract-entities-urls-from-tweets t))
        urls_domains (distinct (extract-domains-from-urls urls))
        urls_titles (map #(try (get-url-title %) 
                               (catch Exception e (.getMessage e)))
                         urls)]
    (render-file "timeline.html" 
                 {:title title 
                  :users (distinct (map :user t))
                  :tweets t
                  :top_words (map #(clojure.string/join ": " %) (most-frequent-n-with-counts filtered_words n))
                  :hashtags (map #(clojure.string/join ": " %) (most-frequent-n-with-counts hashtags n))
                  :urls_domains (map #(clojure.string/join ": " %) (most-frequent-n-with-counts urls_domains n))
                  :urls_titles (map #(clojure.string/join ": " %) (most-frequent-n-with-counts urls_titles n))
                  :user_mentions (map #(clojure.string/join ": " %) (most-frequent-n-with-counts user_mentions n))
                  })))
 

;; (most-frequent-n-with-counts (flatten (map split-text-to-words (map :text tweets))) 20)
