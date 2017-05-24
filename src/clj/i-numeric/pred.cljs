(ns i-numeric.pred
  (:require [i-numeric.util :refer [tap]]))

(defn overflow?
  [min-val max-val val]
  (let [n (tap (js/Number (tap val)) "overflow?")]
    (or
      (and (some? min-val)
           (> min-val n))
      (and (some? max-val)
           (< max-val n)))))

