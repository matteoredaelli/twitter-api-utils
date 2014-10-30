(ns twitter-api-utils.core
  (:require ;; my
   [clojure.tools.cli :refer [parse-opts]]
   )
  (:use 
   [environ.core]
   [twitter-api-utils.twitter]
   [twitter-api-utils.tweets]
   [twitter-api-utils.text])
  (:gen-class))


(def cli-options
  ;; An option with a required argument
  [["-t" 
    "--timeline USERNAME" 
    "username: retreive his/her timeline"
    :default "matteoredaelli"]
   ["-s" 
    "--stopwords FILENAME" 
    "stopword file"
    :default "stopword.txt"]
   ;;["-c" "--count COUNT" "how many tweets to be retreived"
   ;; :default 200
   ;; :parse-fn #(Integer/parseInt %)]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} 
        (parse-opts args cli-options)
        screen-name (:timeline options)
        stopwords (extract-stopwords-from-file (:stopwords options))
        ;;tweets (fetch-user-timeline {:screen-name screen-name} {:count options} 0 [])
        tweets (fetch-user-timeline {:screen-name screen-name} 300 0 [])
        ]
    ;(println (:timeline options))
    (println (report-timeline-html tweets screen-name 10 stopwords))
    ))
    
;; lein run -- -h
;; java -jar target/XXX-0.1.0-SNAPSHOT-standalone.jar -h


    
;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
