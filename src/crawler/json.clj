(ns crawler.json
  (:require [cheshire.core :as json]
            [crawler.graph :as graph]))

(defn node-color [node]
  (case (-> node :args :response-code)
    200 "#0f0"
    500 "#f00"
    404 "#00f"
    nil "#fefefe"
    "#000"))

(defn node-label [node]
  (str (:label (:args node)) " " (:id node)))

(defn prepare-node [input-graph node]
  (let [size (+ (count (graph/edges-from input-graph node))
                (count (graph/edges-to   input-graph node))
                (rand 10))]
    (-> node
        (assoc :label (node-label node))
        (assoc :x (Math/sin size))
        (assoc :y (Math/cos size))
        (assoc :size size)
        (assoc :color (node-color node)))))

(defn prepare-edge [edge]
  edge)

(defn merge-nodes [node1 node2]
  (-> (merge node1 node2)
      (assoc :args
             (merge (:args node1)
                    (:args node2)))))

(def reduce-edges reduce-nodes)

(defn reduce-nodes [coll]
  (->> coll
       (group-by :id)
       vals
       (map (partial reduce merge-nodes))))

(defn graph->json [input-graph]
  (json/generate-string (-> input-graph
                            (assoc :nodes
                                   (map (partial prepare-node input-graph)
                                        (reduce-nodes (:nodes input-graph))))
                            (assoc :edges
                                   (map prepare-edge
                                        (reduce-edges (:edges input-graph)))))))
