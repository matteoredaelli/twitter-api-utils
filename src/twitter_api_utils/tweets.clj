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

(defn tweets-statistics [tweets n stopwords]
  (let [words (extract-words-from-text (extract-cleaned-text-from-tweets tweets))
        filtered_words (remove-stopwords-from-words words stopwords)
        hashtags (extract-entities-hashtags-from-tweets tweets)
        user_mentions (extract-entities-user_mentions-from-tweets tweets)
        urls (distinct (extract-entities-urls-from-tweets tweets))
        urls_domains (distinct (extract-domains-from-urls urls))
        urls_titles (map #(try (get-url-title %) 
                               (catch Exception e (.getMessage e)))
                         urls)]
    {:words words
     :top_words (most-frequent-n-with-counts filtered_words n)
     :hashtags hashtags
     :top_hashtags (most-frequent-n-with-counts hashtags n)
     :user_mentions user_mentions
     :top_user_mentions (most-frequent-n-with-counts user_mentions n)
     :urls urls
     :urls_domains urls_domains
     :urls_titles urls_titles}))

    
(defn timeline-statistics-to-html [tweets stats title]
  (render-file "timeline.html" 
               {:title title 
                :users (distinct (map :user tweets))
                :tweets tweets
                :top_words (map #(clojure.string/join ": " %) (:top_words stats))
                :hashtags (map #(clojure.string/join ": " %) (:top_hashtags stats))
                :urls_domains (distinct (:urls_domains stats))
                :urls_titles (distinct (:urls_titles stats))
                :user_mentions (map #(clojure.string/join ": " %) (:top_user_mentions stats))
                }))
 

;; (most-frequent-n-with-counts (flatten (map split-text-to-words (map :text tweets))) 20)
