(ns neo4j-crawler.core
  (:require [net.cgrand.enlive-html :as html]
            [neo4j-crawler.graph :as graph])
  (:import [java.net URL]))

(def visited (atom #{}))

(defn visited? [s] (@visited (.hashCode s)))

(defn collect-hrefs [url]
  (let [code (.hashCode url)]
    (swap! visited conj code)
    (-> url URL. html/html-resource
        (html/select [:body :a]))))

(def domain "www.lovesoltan.com")
(def root "http://www.lovesoltan.com")

(defn relative-url? [s]
  (not (re-find #"https?://" s)))

(defn convert-local-url [s]
  (if (relative-url? s)
    (str root s)
    s))

(defn current-domain? [s]
  (re-find (re-pattern domain) s))

(defn image-url? [s]
  (re-find #"(jpg|png|jpeg)" s))

(defn follow-url? [s]
  (and (not (visited? s))
       (not (image-url? s))
       (current-domain? s)))
