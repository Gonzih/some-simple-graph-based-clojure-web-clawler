(ns crawler.json
  (:require [cheshire.core :as json]
            [crawler.graph :as graph]))

(defn prepare-node [input-graph node]
  (-> node
      (assoc :x (rand 20))
      (assoc :y (rand 20))
      (assoc :size (+ (count (graph/edges-from input-graph node))
                      (count (graph/edges-to   input-graph node))))
      (assoc :color (if (= 200 (-> node :args :response-code))
                      "#0f0"
                      "#f00"))
      (merge (:args node))))

(defn prepare-edge [edge]
  edge)

(defn distinct-by [fun coll]
  (loop [fun fun
         coll coll
         result []
         cache #{}]
    (if (seq coll)
      (let [item (first coll)
            cache-value (fun item)]
        (recur fun (rest coll)
               (if (cache cache-value)
                 result
                 (conj result item))
               (conj cache cache-value)))
      result)))

(defn graph->json [input-graph]
  (json/generate-string (-> input-graph
                            (assoc :nodes
                                   (map (partial prepare-node input-graph)
                                        (distinct-by :id (:nodes input-graph))))
                            (assoc :edges
                                   (map prepare-edge
                                        (distinct-by :id (:edges input-graph)))))))
