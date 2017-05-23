(ns i-numeric.dom)

(defn get-evt [e]
  (if (some? e) e js/window.event))
