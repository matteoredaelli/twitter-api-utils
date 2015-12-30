(defproject twitter-api-utils "0.2.0"
  :description "Some useful functions about twitter"
  :url "https://github.com/matteoredaelli/twitter-api-utils"
  :license {:name "GPL V3+"
            :url "http://www.gnu.org/copyleft/gpl.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 ;; my
                 [twitter-api "0.7.8"]
                 ;; for JSON
                 ;;[org.clojure/data.json "0.2.4"]
                 [cheshire "5.3.1"]
                 ;; configuration file
                 [environ "1.0.1"]
                 [clojurewerkz/urly "1.0.0"]
                 [selmer "0.9.7"]
                 [http-kit "2.1.19"]
                 [org.clojure/tools.cli "0.3.3"]
                 [com.draines/postal "1.11.3"]
                 ]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]
                   :plugins [[lein-environ "1.0.1"]
                             [cider/cider-nrepl "0.7.0"]]
                   }}
  :main twitter-api-utils.core
  )
