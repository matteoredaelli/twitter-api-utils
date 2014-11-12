;;    Copyright (C) 2014  Matteo Redaelli
;;
;;    This program is free software: you can redistribute it and/or modify
;;    it under the terms of the GNU General Public License as published by
;;    the Free Software Foundation, either version 3 of the License, or
;;    (at your option) any later version.
;;
;;    This program is distributed in the hope that it will be useful,
;;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;    GNU General Public License for more details.
;;
;;   You should have received a copy of the GNU General Public License
;;   along with this program.  If not, see <http://www.gnu.org/licenses/>.

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

(defn extract-stopwords-from-file [filename]
  (set (clojure.string/split (slurp filename) #"\n")))
 
(defn remove-stopwords-from-words [words stopwords]
  (remove stopwords words))
