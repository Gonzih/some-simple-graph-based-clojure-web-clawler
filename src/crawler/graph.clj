(ns crawler.graph
  (:require [clojure.set :refer [union]]))

(defrecord Node [id args])
(defrecord Edge [id source target args])

(defprotocol GraphP
  (merge-graphs [this other])
  (create-node [this id]
               [this id args])
  (create-rel [this source target]
              [this source target args])
  (edges-from [this node])
  (edges-to [this node]))

(defrecord Graph [nodes edges]
  GraphP

  (merge-graphs [this other]
    (Graph. (union (:nodes this)
                   (:nodes other))
            (union (:edges this)
                   (:edges other))))

  (create-node [this id]
    (create-node this id {}))
  (create-node [this id args]
    (assoc this :nodes
           (conj (:nodes this)
                 (Node. id args))))

  (create-rel [this source target]
    (create-rel this source target {}))
  (create-rel [this source target args]
    (assoc this :edges
           (conj
             (:edges this)
             (Edge. (str source "->" target)
                    source target args))))

  (edges-from [this node]
    (filter #(= (:source %) (:id node))
            (:edges this)))

  (edges-to [this node]
    (filter #(= (:target %) (:id node))
            (:edges this))))

(defn init-graph []
  (Graph. #{} #{}))
