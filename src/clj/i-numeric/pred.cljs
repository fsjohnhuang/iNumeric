(ns i-numeric.pred)

(defn overflow?
  [min-val max-val val]
  (let [n (js/Number val)]
    (or
      (and (some? min-val)
           (> min-val val))
      (and (some? max-val)
           (< max-val val)))))

