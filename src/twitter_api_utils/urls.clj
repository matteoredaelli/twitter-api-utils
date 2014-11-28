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
  (let [headers (:headers (clj-http.client/head url {:socket-timeout 5000 :conn-timeout 5000}))
        content-type (headers "Content-Type")
        is-html (count (re-seq #"text/html" content-type))]
    (if is-html
      (let [body (:body clj-http.client/get url{:socket-timeout 5000 :conn-timeout 5000} )
            title (clojure.string/trim (nth (re-find #"<title>(.*)</title>" body) 1))]
        title)
      content-type)))

(defn safe-get-url-title [url]
  (try (get-url-title url)
       ;;(catch Exception e (.getMessage e))))
       (catch Exception e nil)))
