(ns i-numeric.util)

(defn tap
  "打印值"
  ([x] (println "log:" x) x)
  ([x msg] (println msg ":" x) x))

(defn- inspect-1 [expr]
	`(let [result# ~expr]
	   (js/console.info (str (pr-str '~expr) "=>" (pr-str result#)))
		 result#))
(defmacro inspect [& exprs]
	`(do ~@(map inspect-1 exprs)))

(defn nil-or-empty?
  "判断是否为nil或空"
  [x]
  (or (nil? x) (empty? x)))
