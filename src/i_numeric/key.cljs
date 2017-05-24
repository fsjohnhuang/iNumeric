(ns i-numeric.key
  (:require [i-numeric.util :refer [tap]]))

(defn num-in-main? [key-code]
  (reduce (fn [accu v]
            (and accu (v key-code)))
          true
          [#(<= 48 %) #(>= 57 %)]))

(defn num-in-numpad? [key-code]
  (reduce (fn [accu v]
            (and accu (v key-code)))
          true
          [#(<= 96 %) #(>= 105 %)]))

(defn num-key? [key-code]
  (or (num-in-main? key-code)
      (num-in-numpad? key-code)))

(defn to-num
  "将keyCode转换为数字"
  [key-code]
  (let [n (- key-code 96)]
    (if (neg? n) (- key-code 48) n)))

(defn arrow-left? [key-code]
  (= 37 key-code))

(defn arrow-up? [key-code]
  (= 38 key-code))

(defn arrow-right? [key-code]
  (= 39 key-code))

(defn arrow-down? [key-code]
  (= 40 key-code))

(defn arrow-key? [key-code]
  (or (arrow-up? key-code)
      (arrow-down? key-code)
      (arrow-left? key-code)
      (arrow-right? key-code)))

(defn delete? [key-code]
  (= 46 key-code))

(defn backspace? [key-code]
  (= 8 key-code))

(defn minus? [key-code]
  (or (= 109 key-code)
      (= 189 key-code)))

(defn in-ime? [key-code]
  (= 229 key-code))

(defn dot-in-numpad? [key-code]
  (= 110 key-code))

(defn dot-in-main? [key-code]
  (= 190 key-code))

(defn dot? [key-code]
  (or (dot-in-numpad? key-code)
      (dot-in-main? key-code)))
