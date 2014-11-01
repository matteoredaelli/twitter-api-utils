(ns twitter-api-utils.core
  (:require ;; my
   [clojure.tools.cli :refer [parse-opts]]
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
  [["-t" 
    "--timeline USERNAME" 
    "username: retreive his/her timeline"
    :default "matteoredaelli"]
   ["-s" 
    "--stopwords FILENAME" 
    "stopword file"
    :default "stopwords.txt"]
   ["-T" 
    "--to EMAIL" 
    "email address"
    :default "XXXX@blogspt.com"]
  ["-F" 
    "--from EMAIL" 
    "email address"
    :default "XXXX@blogspt.com"]
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
        from (:from options)
        to (:to options)
        stopwords (extract-stopwords-from-file (:stopwords options))
        ;;tweets (fetch-user-timeline {:screen-name screen-name} {:count options} 0 [])
        ;; today
        now (.getTime (java.util.Calendar/getInstance))
        f2  (java.text.SimpleDateFormat. "MMMM_yyyy")
        today-string (clojure.string/lower-case (.format f2 now))

        tweets (fetch-user-timeline {:screen-name screen-name} 300 0 [])
        body (report-timeline-html tweets screen-name 10 stopwords)
        ]

    (send-message {:host "localhost"}
                  {:from [from]
                   :to [to]
                   :subject (str screen-name "-twitter-" today-string)
                   :body [{:type "text/html; charset=utf-8"
                           :content body}]
                   
                   })))
;; lein run -- -h
;; java -jar target/XXX-0.1.0-SNAPSHOT-standalone.jar -h


    
;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
