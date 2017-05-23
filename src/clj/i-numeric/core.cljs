(ns i-numeric.core
  (:require [clojure.browser.repl :as repl]
            [i-numeric.dom :as dom]
            [i-numeric.util :as util]
            [i-numeric.key :as ikey]
            [i-numeric.pred :as pred]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(def ^:const STEP 1)
(defn ^:const WITH-CTRL 0.1)
(defn ^:const WITH-SHIFT 10)

(defn keydown-handler
  [evt]
  (let [e (dom/get-evt evt)
        el (dom/get-target e)
        key-code (dom/get-key-code e)
        attr (partial dom/attr el)
        value (dom/prop el "value")
        num-value (js/parseInt (if (or (nil? value) (empty? value)) "0" value))
        min-val (attr "min")
        max-val (attr "max")
        precision (attr "precision")
        step (attr "step" STEP)
        overflow? (partial pred/overflow? min-val max-val)]
    (if
      (overflow?
        (cond
          (ikey/num-key? key-code)    (str value (ikey/to-num key-code))
          (ikey/arrow-up? key-code)   (+ num-value (* (cond (dom/with-ctrl? e) WITH-CTRL
                                                            (dom/with-shift? e) WITH-SHIFT
                                                            :else 1) step)
          (ikey/arrow-down? key-code) (- num-value (* (cond (dom/with-ctrl? e) WITH-CTRL
                                                            (dom/with-shift? e) WITH-SHIFT
                                                            :else 1) step))))
      (dom/prevent-default! e))))

(defn ^:export inc [step v] (+ step v))
(defn ^:export dec [step v] (- v step))
(defn ^:export init
  [el]
  (dom/listen! el "keydown" keydown-handler)
  (dom/listen! el "keyup" keyup-handler))

;; 上限限制
;; 下限限制
;; 精度限制

;; 场景:非输入法, 拦截keydown作预判

;; 场景:输入法,拦截keyup作补救

;; 拦截例外, 0-9, ., delete, baskspace, arrow left, arrow right
;; 附加，arrow-up -> (partial inc step), arrow-down -> (partial dec step)
;;       shift + arrow-up -> (partial inc (* 10 step))
;;       shift + arrow-down -> (partial dec (* 10 step))
;;       ctrl + arrow-up -> (partial inc (* 0.1 step))
;;       ctrl + arrow-down -> (partial dec (* 0.1 step))

