(ns i-numeric.pred
  (:require [i-numeric.util :refer [tap]]))

(defn gt-max?
  [max-val val]
  (let [n (js/Number val)]
    (and (some? max-val)
         (< max-val n))))

(defn lt-min?
  [min-val val]
  (let [n (js/Number val)]
    (and (some? min-val)
         (> min-val n))))

(defn overflow?
  [min-val max-val val]
  (or (gt-max? max-val val)
      (lt-min? min-val val)))
