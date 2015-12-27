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
   [org.httpkit.client :as http]
   )
  (:use 
   [clojurewerkz.urly.core]
   [environ.core]
   )) 

(defn extract-domain-from-url
  [url]
  (str (protocol-of url) "://" (host-of url)))

(defn extract-domains-from-urls
  [urls]
  (map extract-domain-from-url urls))

(defn get-real-url
  [url]
  (let [resp (http/head url)]
      (-> @resp :opts :url)))

(defn get-url-title [url]
  (let [resp (http/head url)]
    (let [headers (:headers @resp)
          content-type (:content-type headers)
          is-html (count (re-seq #"text/html" content-type))]
      (if is-html
        (let [resp (http/get url)]
          (let [body (:body @resp)
                title (clojure.string/trim (nth (re-find #"<title>(.*)</title>" body) 1))]
               title))
        content-type))))

(defn safe-get-url-title [url]
  (try (get-url-title url)
       ;;(catch Exception e (.getMessage e))))
       (catch Exception e nil)))
