(ns twitter-api-utils.utils
  (:use 
   [environ.core]
   )) 

(defn most-frequent-n-with-counts [n items]
  (->> items
    frequencies
    (sort-by val)
    reverse
    (take n)))

(defn most-frequent-n [n items]
  (map first (most-frequent-n-with-counts n items)))

(defn split-text-to-words [text]
  (re-seq #"\w+" text))
 
