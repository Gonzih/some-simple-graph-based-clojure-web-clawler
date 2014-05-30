(ns crawler.util)

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
