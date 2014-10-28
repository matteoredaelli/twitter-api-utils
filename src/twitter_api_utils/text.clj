(ns twitter-api-utils.text
  (:require ;; my
   [cheshire.core :refer :all]
   )
  (:use 
   [twitter-api-utils.utils]
   [selmer.parser])) 


(defn extract-words-from-text [text]
  (re-seq #"\w+" text))

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
                                                        "RT "
                                                        " ")
                                other-options)
       "remove-urls" (clean-text (remove-urls-from-text text)
                                 other-options)
       "remove-extra-whitespaces" (clean-text (remove-extra-whitespaces-from-text text)
                                  other-options)
       "tolower" (clean-text (clojure.string/lower-case text) other-options)
       )))
