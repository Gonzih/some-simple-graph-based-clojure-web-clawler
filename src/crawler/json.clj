(ns crawler.json
  (:require [cheshire.core :as json]
            [crawler.graph :as graph]))

(defn node-color [node]
  (case (-> node :args :response-code)
    200 "#0f0"
    500 "#f00"
    404 "#e00000"
    nil "#eeeeee"
    "#000"))

(defn node-label [node]
  (str (:label (:args node)) " - " (:id node)))

(defn prepare-node [input-graph node]
  (-> node
      (assoc :label (node-label node))
      (assoc :x (rand 20))
      (assoc :y (rand 20))
      (assoc :size (+ (count (graph/edges-from input-graph node))
                      (count (graph/edges-to   input-graph node))))
      (assoc :color (node-color node))))

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
