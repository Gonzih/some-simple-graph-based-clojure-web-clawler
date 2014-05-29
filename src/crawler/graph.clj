(ns crawler.graph
  (:require [clojure.set :refer [union]]))

(defrecord Node [id args])
(defrecord Relation [source target args])

(defprotocol GraphP
  (merge-graphs [this other])
  (create-node [this id]
               [this id args])
  (create-rel [this source target]
              [this source target args]))

(defrecord Graph [nodes relations]
  GraphP
  (merge-graphs [this other]
    (Graph. (union (:nodes this)
                   (:nodes other))
            (union (:relations this)
                   (:relations other))))
  (create-node [this id]
    (create-node this id {}))
  (create-node [this id args]
    (assoc this :nodes
           (conj (:nodes this)
                 (Node. id args))))
  (create-rel [this source target]
    (create-rel this source target {}))
  (create-rel [this source target args]
    (assoc this :relations
           (conj
             (:relations this)
             (Relation. source target args)
             (Relation. target source args)))))

(defn init-graph []
  (Graph. #{} #{}))
