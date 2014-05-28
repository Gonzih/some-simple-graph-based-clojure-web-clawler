(ns neo4j-crawler.graph)

(defrecord Node [id args])
(defrecord Relation [source targets])

(defprotocol GraphP
  (create-node [this id args])
  (create-rel [this source target]))

(defrecord Graph [nodes relations]
  GraphP
  (create-node [this id args]
    (assoc this :nodes
           (conj (:nodes this)
                 (Node. id args))))
  (create-rel [this source target]
    (assoc this :relations
           (merge-with conj
                       (:relations this)
                       {source #{target}
                        target #{source}}))))

(defn init-graph []
  (Graph. #{} {}))

(-> (init-graph)
    (create-node "page" {})
    (create-node "page2" {})
    (create-rel "page" "page2"))
