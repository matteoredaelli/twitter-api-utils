(ns twitter-api-utils.tweets
  (:require ;; my
   [cheshire.core :refer :all]
   )
  (:use 
   [twitter-api-utils.urls]
   [twitter-api-utils.utils]
   [selmer.parser])) 

(defn oldest-id [tweets]
  (apply min (map :id tweets)))

(defn remove-urls-from-text [text]
  (clojure.string/replace text #"http[s]*://[^ ]+" ""))

(defn remove-extra-whitespaces-from-text [text]
  (clojure.string/trim (clojure.string/replace text #"[ ]{2,}+", " ")))

(defn clean-text [text options]
  (let [option (first options)
        other-options (rest options)]
    (case option
       nil text
       [] text
       "remove-RTs" (clean-text (clojure.string/replace text
                                                        #"^RT @[^ ]+:"
                                                        "")
                                other-options)
       "remove-urls" (clean-text (remove-urls-from-text text)
                                 other-options)
       "remove-extra-whitespaces" (clean-text (remove-extra-whitespaces-from-text text)
                                  other-options))))
   
(defn clean-tweets
  ([tweets]
   (map #(clean-text (:text %)
                     ["remove-RTs" 
                      "remove-urls"
                      "remove-extra-whitespaces"])
        tweets))
  ([tweets options]
   (map #(clean-text (:text %) options) tweets))
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

(defn extract-entities-user_mentions-from-tweets
  [tweets]
  (map :screen_name (flatten (map #(get-in % [:entities :user_mentions]) tweets))))

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
  (let [;;t1 (top-tweets-with-retweets tweets n)
        ;;t2 (top-tweets-with-favorites tweets n)
        ;;t (distinct (concat t1 t2))
        t tweets
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
                  :hashtags (map #(clojure.string/join ": " %) (most-frequent-n-with-counts hashtags n))
                  :urls_domains (map #(clojure.string/join ": " %) (most-frequent-n-with-counts urls_domains n))
                  :urls_titles (distinct urls_titles)
                  :user_mentions (map #(clojure.string/join ": " %) (most-frequent-n-with-counts user_mentions n))
                  })))
 

;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))

;; (most-frequent-n-with-counts (flatten (map split-text-to-words (map :text tweets))) 20)
