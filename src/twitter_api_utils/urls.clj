(ns twitter-api-utils.urls
  (:require ;; my
   [clj-http.client]
   )
  (:use 
   [clojurewerkz.urly.core]
   [environ.core]
   )) 

(defn extract-domains-from-urls
  [urls]
  (map #(str (protocol-of %) "://" (host-of %)) urls))


;; (map #(try (get-url-title %) (catch Exception e (.getMessage e))) u)
(defn get-url-title [url]
  (let [headers (:headers (clj-http.client/head url))
        content-type (headers "Content-Type")
        is-html (count (re-seq #"text/html" content-type))]
    (if is-html
      (let [body (:body (clj-http.client/get url))
            title (clojure.string/trim (nth (re-find #"<title>(.*)</title>" body) 1))]
        title)
      content-type)))
