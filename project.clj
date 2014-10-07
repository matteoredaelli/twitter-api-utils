(defproject twitter-api-utils "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;; my
                 [twitter-api "0.7.5"]
                 ;; for JSON
                 ;;[org.clojure/data.json "0.2.4"]
                 [cheshire "5.3.1"]
                 ;; configuration file
                 [environ "0.5.0"]
                 ]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]
                   :plugins [[lein-environ "0.5.0"]]
                   }}
  )
