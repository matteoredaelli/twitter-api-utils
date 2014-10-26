(ns twitter-api-utils.core
  (:require ;; my
   [clojure.tools.cli :refer [parse-opts]]
   )
  (:use 
   [environ.core]
   [twitter-api-utils.twitter]
   [twitter-api-utils.tweets])
  (:gen-class))


(def cli-options
  ;; An option with a required argument
  [["-t" "--timeline" "First argument is an user, retreive his/her timeline"]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} 
        (parse-opts args cli-options)
        screen-name (:first arguments)
        tweets (fetch-user-timeline {:screen-name screen-name} 100 0 [])
        ]
    ;;(println (first arguments))
    (println (report-timeline-html tweets screen-name 10))
    ))
    
;; lein run -- -h
;; java -jar target/XXX-0.1.0-SNAPSHOT-standalone.jar -h


    
;; (def t (fetch-user-timeline-single {:screen-name "Pirelli_Media"}))
;; (def u (extract-entities-urls-from-tweets t))
;; (get-url-title (nth u 2))
