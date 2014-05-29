(ns crawler.json
  (:require [cheshire.core :as json]))

(defn graph->json [graph]
  (json/generate-string graph))
