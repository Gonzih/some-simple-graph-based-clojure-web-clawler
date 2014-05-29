(ns crawler.core
  (:require [clojure.pprint :refer [pprint]]
            [crawler.walker :as walker]
            [crawler.graph :as graph]
            [crawler.json :as json]))

(def root "http://localhost:4000")

(defn -main [& args]
  (let [resulting-graph (time (walker/walk-row (graph/init-graph)
                                               [{:current root :parent :none}]
                                               #{}
                                               {:root root}))])
  (shutdown-agents))
