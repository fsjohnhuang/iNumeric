(ns i-numeric.pred
  (:require [i-numeric.util :refer [tap]]
            [i-numeric.key :refer [num-key? arrow-key? dot?
                                   delete? backspace? minus?
                                   in-ime?]]))

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

(defn valid-key?
  [key-code]
  (reduce (fn [accu f]
            (or accu (f key-code)))
          false
          [num-key? arrow-key? dot? delete? backspace? minus? in-ime?]))
