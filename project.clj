(defproject twitter-api-utils "0.3.0-SNAPSHOT"
  :description "Some useful functions about twitter"
  :url "https://github.com/matteoredaelli/twitter-api-utils"
  :license {:name "GPL V3+"
            :url "http://www.gnu.org/copyleft/gpl.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;; https://mvnrepository.com/artifact/javax.activation/activation
                 [javax.activation/activation "1.1"]

                 ;; my
                 [twitter-api "1.8.0"]
                 ;; for JSON
                 ;;[org.clojure/data.json "0.2.4"]
                 [cheshire "5.9.0"]
                 ;; configuration file
                 [environ "1.0.1"]
                 [clojurewerkz/urly "1.0.0"]
                 [selmer "1.12.17"]
                 [http-kit "2.4.0-alpha3"]
                 [org.clojure/tools.cli "0.4.2"]
                 [com.draines/postal "1.11.3"]
                 ]
  ;;:jvm-opts ["--add-modules" "java.xml.bind"]
  :sign-releases false
  
  :profiles {:dev {;:dependencies [[javax.servlet/servlet-api "2.5"]]
                   :plugins [[lein-environ "1.1.0"]
                             ]
                   }}
  :main twitter-api-utils.core
  :aot [twitter-api-utils.core]
  )
