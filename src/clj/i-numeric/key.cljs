(ns i-numeric.key)

(defn num-in-main? [key-code]
  (reduce (fn [accu v]
            (and accu (v key-code)))
          true
          [#(<= 48 %) #(>= 52 %)]))

(defn num-in-numpad? [key-code]
  (reduce (fn [accu v]
            (and accu (v key-code)))
          true
          [#(<= 96 %) #(>= 105 %)]))

(defn num-key? [key-code]
  (or (num-in-main? key-code)
      (num-in-numpad key-code)))

(defn to-num
  "将keyCode转换为数字"
  [key-code]
  (let [n (- key-code 96)]
    (if (neg? n) (- key-code 48) n)))

(defn arrow-up? [key-code]
  (= 38 key-code))

(defn arrow-right? [key-code]
  (= 39 key-code))

(defn arrow-down? [key-code]
  (= 40 key-code))

(defn arrow-left? [key-code]
  (= 41 key-code))

(defn delete? [key-code]
  (= 46 key-code))

(defn dot-in-numpad? [key-code]
  (= 110 key-code))

(defn dot-in-main? [key-code]
  (= 190 key-code))

(defn dot? [key-code]
  (or (dot-in-numpad? key-code)
      (dot-in-main? key-code)))
