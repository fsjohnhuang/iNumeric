(ns i-numeric.core
  (:require [clojure.browser.repl :as repl]
            [i-numeric.dom :as dom]
            [i-numeric.util :refer [tap nil-or-empty? inspect]]
            [i-numeric.key :as ikey]
            [i-numeric.pred :as pred]))

(enable-console-print!)

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(def ^:const STEP 1)
(def ^:const WITH-CTRL 0.1)
(def ^:const WITH-SHIFT 10)

(defn arrow-up-with-alt?
  [e]
  (and (-> e dom/get-key-code ikey/arrow-up?)
       (dom/with-alt? e)))
(defn arrow-up-with-alt
  [v step]
  (/ (+ (* 10 v) (* 1 step)) 10))

(defn arrow-down-with-alt?
  [e]
  (and (-> e dom/get-key-code ikey/arrow-down?)
       (dom/with-alt? e)))
(defn arrow-down-with-alt
  [v step]
  (/ (- (* 10 v) (* 1 step)) 10))

(defn arrow-up-with-shift?
  [e]
  (and (-> e dom/get-key-code ikey/arrow-up?)
       (dom/with-shift? e)))
(defn arrow-up-with-shift
  [v step]
  (+ v (* 10 step)))

(defn arrow-down-with-shift?
  [e]
  (and (-> e dom/get-key-code ikey/arrow-down?)
       (dom/with-shift? e)))
(defn arrow-down-with-shift
  [v step]
  (- v (* 10 step)))


(defn keydown-handler
  [evt]
  (let [e (dom/get-evt evt)
        el (dom/get-target e)
        key-code (dom/get-key-code e)
        attr (partial dom/attr el)
        value (dom/prop el "value" "")
        num-value (js/parseFloat (if (nil-or-empty? value) "0" value))
        min-val (attr "min")
        max-val (attr "max")
        precision (attr "precision" js/Number.MAX_SAFE_INTEGER)
        r-pattern (js/RegExp (str "^([0-9]*)(?:(\\.[0-9]{0," precision "})(.*))?$"))
        step (js/parseFloat (attr "step" STEP))
        overflow? (partial pred/overflow? min-val max-val)
        changed-val (cond
                      (ikey/num-key? key-code) (->> key-code ikey/to-num (str value) js/parseFloat)
                      (ikey/dot? key-code) (js/parseFloat (str value ".0"))
                      (arrow-up-with-alt? e) (arrow-up-with-alt num-value step)
                      (arrow-down-with-alt? e) (arrow-down-with-alt num-value step)
                      (arrow-up-with-shift? e) (arrow-up-with-shift num-value step)
                      (arrow-down-with-shift? e) (arrow-down-with-shift num-value step)
                      (ikey/arrow-up? key-code) (+ num-value step)
                      (ikey/arrow-down? key-code) (- num-value step))
        matches (tap (re-matches r-pattern (str changed-val)))
        m-val (if (some? matches) (js/parseFloat (str (nth matches 1) (nth matches 2))) (js/parseFloat min-val))]
    (cond
      (overflow? m-val)
      (dom/prevent-default! e)

      (or (ikey/arrow-up? key-code)
          (ikey/arrow-down? key-code)
          (arrow-up-with-shift? e)
          (arrow-down-with-shift? e)
          (arrow-up-with-alt? e)
          (arrow-down-with-alt? e))
      (do
        (dom/prevent-default! e)
        (dom/prop! el "value" m-val))

      (not (nil-or-empty? (nth matches 3)))
      (do
        (dom/prevent-default! e)
        (dom/prop! el "value" m-val)))))

(defn keyup-handler
  [evt]
  (let [e (dom/get-evt evt)
        el (dom/get-target e)
        attr (partial dom/attr el)
        value (dom/prop el "value" "")
        min-val (attr "min")
        max-val (attr "max")
        precision (attr "precision" js/Number.MAX_SAFE_INTEGER)
        r-pattern (js/RegExp (tap (str "^([+-]?[0-9]*)(\\.[0-9]{0," precision "})?(.*)$")))
        overflow? (partial pred/overflow? min-val max-val)
        matches (tap (re-matches r-pattern (str value)) "matches")
        m-val (if (some? matches) (str (nth matches 1) (nth matches 2)) min-val)
        f-val (cond (pred/gt-max? max-val m-val) max-val (pred/lt-min? min-val m-val) min-val :else m-val)]
      (dom/prop! el "value" f-val)))

(defn ^:export inc [step v] (+ step v))
(defn ^:export dec [step v] (- v step))

(defn ^:export init
  [el]
  (dom/listen! el "keydown" keydown-handler))
  ;;(dom/listen! el "keyup" keyup-handler))

(init (-> "input" dom/$ seq first))

;; 上限限制
;; 下限限制
;; 精度限制

;; 场景:非输入法, 拦截keydown作预判

;; 场景:输入法,拦截keyup作补救

;; 拦截例外, 0-9, ., delete, baskspace, arrow left, arrow right
;; 附加，arrow-up -> (partial inc step), arrow-down -> (partial dec step)
;;       arrow-up 的默认行为是光标移动到行首
;;       arrow-down 的默认行为是光标移动到行尾
;;       shift + arrow-up -> (partial inc (* 10 step))
;;       shift + arrow-down -> (partial dec (* 10 step))
;;       ctrl + arrow-up -> (partial inc (* 0.1 step))
;;       ctrl + arrow-down -> (partial dec (* 0.1 step))

