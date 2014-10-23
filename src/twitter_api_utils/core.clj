(ns twitter-api-utils.core
  (:require ;; my
   [clojure.tools.cli :refer [parse-opts]]
   )
  (:use 
   [environ.core]
   [twitter-api-utils.twitter]
   [twitter-api-utils.tweets]
   [twitter-api-utils.urls])
  (:gen-class))


(def cli-options
  ;; An option with a required argument
  [["-t" "--timeline" "Timeline of this screen-name"
    :default "matteoredaelli"]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} 
        (parse-opts args cli-options)
        screen-name (:timeline options)
        tweets (fetch-user-timeline-single {:screen-name screen-name})
        urls (extract-entities-urls-from-tweets tweets)
        ]
    (println (clojure.string/join \newline urls))))
    
    
;; lein run -- -h
;; java -jar target/XXX-0.1.0-SNAPSHOT-standalone.jar -h


    
;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
