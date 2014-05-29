(ns crawler.core
  (:require [clojure.pprint :refer [pprint]]
            [crawler.walker :as walker]
            [crawler.graph :as graph]
            [crawler.json :as json]))

(def root "http://localhost:4000/")

(def walk (memoize walker/walk-row))

(defn -main [& args]
  (let [resulting-graph (time (walk (graph/init-graph)
                                    [{:current root :parent "start"}]
                                    #{}
                                    {:root root}))]
    (spit "tmp/data.json" (json/graph->json resulting-graph)))
  (shutdown-agents))
