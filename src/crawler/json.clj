(ns crawler.json
  (:require [cheshire.core :as json]))

(defn prepare-node [node]
  (-> node
      (assoc :x (rand 20))
      (assoc :y (rand 20))
      (assoc :size (rand 5))
      (assoc :color "#f00")
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

(defn graph->json [graph]
  (json/generate-string (-> graph
                            (assoc :nodes
                                   (map prepare-node
                                        (distinct-by :id (:nodes graph))))
                            (assoc :edges
                                   (map prepare-edge
                                        (distinct-by :id (:edges graph)))))))
