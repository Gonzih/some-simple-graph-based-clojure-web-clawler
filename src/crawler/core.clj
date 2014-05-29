(ns crawler.core
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [crawler.walker :as walker]
            [crawler.graph :as graph]))

(def root "http://localhost:4000")

(defn -main [& args]
  (pprint (walker/walk-row (graph/init-graph)
                           [{:current root :parent :none}]
                           #{}
                           {:root root}))
  (shutdown-agents))
