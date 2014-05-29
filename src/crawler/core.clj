(ns crawler.core
  (:require [net.cgrand.enlive-html :as html]
            [crawler.graph :as graph]
            [clojure.pprint :refer [pprint]])
  (:import [java.net URL]))

(defn visited? [cache s] (cache s))

(def domain "www.lovesoltan.com")
(def root "http://www.lovesoltan.com")

(defn relative-url? [s]
  (not (re-find #"https?://" s)))

(defn convert-relative-url [{:keys [current parent] :as url}]
  (let [new-current (if (relative-url? current)
                      (str root current)
                      current)]
    (assoc url :current new-current)))

(defn current-domain? [current]
  (= (.getHost (URL. current))
     domain))

(defn image-url? [current]
  (re-find #"(jpg|png|jpeg)" current))

(defn follow-url? [cache {:keys [current parent] :as data}]
  (and (not (nil? current))
       (not (visited? cache current))
       (not (image-url? current))
       (current-domain? current)))

(defn build-graph-for-url [{:keys [current parent]}]
  (let [url (URL. current)
        resource (html/html-resource url)
        tags (-> resource (html/select [:body :a]))
        urls (map (comp (fn [url] {:current url :parent current})
                        :href :attrs)
                  tags)
        title (apply str (-> resource
                             (html/select [:head :title])
                             first :content))
        graph (if (= current parent)
                (graph/init-graph)
                (-> (graph/init-graph)
                    (graph/create-node current)
                    (graph/create-node parent)
                    (graph/create-rel current parent)))]
    [graph urls]))

(defn walk-row [graph urls cache]
  (if (seq urls)
    (let [pairs-of-graph-url (pmap build-graph-for-url urls)
          new-graphs (map first pairs-of-graph-url)
          new-urls (->> pairs-of-graph-url
                        (map second)
                        flatten
                        (map convert-relative-url)
                        (filter (partial follow-url? cache)))
          new-graph (reduce graph/merge-graphs graph new-graphs)
          new-cache (into cache (map :current urls))]
      (recur new-graph new-urls new-cache))
    graph))

(defn -main [& args]
  (pprint (walk-row (graph/init-graph) [{:current root :parent :none}] #{}))
  (shutdown-agents))
