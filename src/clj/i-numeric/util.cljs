(ns i-numeric.util)

(defn tap
  "打印值"
  ([x] (println "log:" x) x)
  ([x msg] (println msg ":" x) x))

(defn nil-or-empty?
  "判断是否为nil或空"
  [x]
  (or (nil? x) (empty? x)))
