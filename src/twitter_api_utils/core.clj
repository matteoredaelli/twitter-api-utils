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

(ns twitter-api-utils.core
  (:require ;; my
   [clojure.tools.cli :refer [parse-opts]]
   [cheshire.core :refer :all]
   )
  (:use 
   [environ.core]
   [twitter-api-utils.twitter]
   [twitter-api-utils.tweets]
   [twitter-api-utils.text]
   [postal.core])
  (:gen-class))


(def cli-options
  ;; An option with a required argument
  [["-u" 
    "--user USERNAME" 
    "username: retreive his/her timeline"
    :default "matteoredaelli"]
   ["-s" 
    "--stopwords FILENAME" 
    "stopword file"
    :default "stopwords.txt"]
   ["-T" 
    "--to EMAIL" 
    "email address"
    :default nil]
   ["-F" 
    "--from EMAIL" 
    "email address"
    :default "XXXX@blogspt.com"]
   ["-c"
    "--count COUNT" "how many tweets to be retreived"
    :default 200
    :parse-fn #(Integer/parseInt %)]
   ["-t"
    "--top COUNT" "extract TOP n"
    :default 5
    :parse-fn #(Integer/parseInt %)]
   ["-j" "--json"
    :default 0]
   ["-H" "--html"
    :default 0]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} 
        (parse-opts args cli-options)
        screen-name (:user options)
        count (:count options)
        from (:from options)
        to (:to options)
        top (:top options)
        stopwords (extract-stopwords-from-file (:stopwords options))
        ;;tweets (fetch-user-timeline {:screen-name screen-name} {:count options} 0 [])
        ;; today
        now (.getTime (java.util.Calendar/getInstance))
        f2  (java.text.SimpleDateFormat. "MMMM_yyyy")
        today-string (clojure.string/lower-case (.format f2 now))
        tweets (fetch-user-timeline {:screen-name screen-name} count 0 [])
        stats (tweets-statistics tweets top stopwords)
        ]
    (cond (:json options) (println (generate-string stats))
          (:html options) (println (timeline-statistics-to-html tweets stats screen-name))
          to (let [body (timeline-statistics-to-html tweets stats screen-name)]
               (send-message {:host "localhost"}
                             {:from [from]
                              :to [to]
                              :subject (str screen-name "-twitter-" today-string)
                              :body [{:type "text/html; charset=utf-8"
                                      :content body}]
                              })))))
    
;; lein run -- -h
;; java -jar target/XXX-0.1.0-SNAPSHOT-standalone.jar -h


;; lein run -- -u matteoredaelli -s stopwords.txt -c 10 -j -t 3
