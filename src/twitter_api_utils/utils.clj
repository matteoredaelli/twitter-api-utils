(ns twitter-api-utils.utils
  (:use 
   [environ.core]
   )) 

(defn most-frequent-n-with-counts [items n]
  (->> items
    frequencies
    (sort-by val)
    reverse
    (take n)))

(defn most-frequent-n [items n]
  (map first (most-frequent-n-with-counts items n)))

(defn split-text-to-words [text]
  (re-seq #"\w+" text))
 
