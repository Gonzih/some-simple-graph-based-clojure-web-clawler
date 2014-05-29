(ns crawler.walker
  (:require [net.cgrand.enlive-html :as html]
            [crawler.graph :as graph]
            [clojure.pprint :refer [pprint]])
  (:import [java.net URL]))

(defn visited? [cache s] (cache s))

(defn relative-url? [s]
  (not (re-find #"https?://" s)))

(defn convert-relative-url [root {:keys [current parent] :as data}]
  (let [new-current (if (relative-url? current)
                      (str root current)
                      current)]
    (assoc data :current new-current)))

(defn host-with-port [link]
  (let [url (URL. link)]
    (if (< 0 (.getPort url))
      (str (.getHost url) ":" (.getPort url))
      (.getHost url))))

(defn current-domain? [current domain]
    (= (host-with-port current) domain))

(defn image-url? [current]
  (re-find #"(jpg|png|jpeg)" current))

(defn follow-url? [cache domain {:keys [current parent] :as data}]
  (and (not (nil? current))
       (not (visited? cache current))
       (not (image-url? current))
       (current-domain? current domain)))

(defn tag-to-url-map [tag current response-code]
  (let [content (apply str (:content tag))
        url (-> tag :attrs :href)]
    {:parent current
     :current url
     :link-text content}))

(defn build-graph-for-200 [current parent connection link-text response-code]
  (let
    [input-stream (.getContent connection)
     resource (html/html-resource input-stream)
     tags (-> resource (html/select [:body :a]))
     urls (map #(tag-to-url-map % current response-code) tags)
     title (apply str (-> resource
                          (html/select [:head :title])
                          first :content))
     graph (if (= current parent)
             (graph/init-graph)
             (-> (graph/init-graph)
                 (graph/create-node current
                                    {:response-code response-code
                                     :link-text link-text})
                 (graph/create-node parent)
                 (graph/create-rel current parent)))]
    [graph urls]))

(defn build-graph-for-non-200 [current parent connection link-text response-code]
  (let [graph (-> (graph/init-graph)
                  (graph/create-node current
                                     {:response-code response-code
                                      :link-text link-text})
                  (graph/create-node parent)
                  (graph/create-rel current parent))]
  [graph []]))

(defn build-graph-for-url [{:keys [current parent link-text]}]
  (try
    (let [url (URL. current)
          connection (.openConnection url)
          response-code (.getResponseCode connection)]
      (if (= 200 response-code)
        (build-graph-for-200 current parent connection link-text response-code)
        (build-graph-for-non-200 current parent connection link-text response-code)))
    (catch Exception e
      (println "Exception " e " while parsing " current)
      (throw e))))

(defn walk-row [graph urls cache {:keys [root]}]
  (if (seq urls)
    (let [domain (host-with-port root)
          pairs-of-graph-url (pmap build-graph-for-url urls)
          new-graphs (map first pairs-of-graph-url)
          new-urls (->> pairs-of-graph-url
                        (map second)
                        flatten
                        (map (partial convert-relative-url root))
                        (filter (partial follow-url? cache domain)))
          new-graph (reduce graph/merge-graphs graph new-graphs)
          new-cache (into cache (map :current urls))]
      (recur new-graph new-urls new-cache {:root root}))
    graph))
