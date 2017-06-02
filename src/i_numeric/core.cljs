(ns i-numeric.core
  (:require [i-numeric.dom :as dom]
            [i-numeric.util :refer [tap nil-or-empty?]]
            [i-numeric.key :as ikey]
            [i-numeric.pred :as pred]))

(defn trace [x] (println x) x)

(enable-console-print!)

(def ^:const STEP 1)

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
        min-val (attr "min")
        precision (js/parseFloat (attr "precision" js/Number.MAX_SAFE_INTEGER))]
    (cond
      ;; filters invalid key press action or negative mark when the value of attribute min is positive.
      (or (not (pred/valid-key? key-code))
          (and (<= 0 min-val)
               (ikey/minus? key-code))
          (and (= 0 precision)
               (ikey/dot? key-code)))
      (dom/prevent-default! e)

      ;; skip when press delete, baskspace, arrow-left, arrow-right
      (or (ikey/delete? key-code)
          (ikey/backspace? key-code)
          (ikey/arrow-left? key-code)
          (ikey/arrow-right? key-code))
      true

      ;; fire keyup event when do key pressing in IME
      (ikey/in-ime? key-code)
      (dom/dispatch-event el "keyup")

      ;; the hardest work is comming :D
      :else
      (let [max-val (attr "max")
            step (js/parseFloat (attr "step" STEP))
            matcher (partial
                      re-matches
                      (as-> precision $
                        (str "^([-]?[0-9]*)(?:(\\.[0-9]{0," $ "}))?(.*)$")
                        (js/RegExp $)))
            overflow? (partial pred/overflow? min-val max-val)
            value (dom/prop el "value" "")
            num-value (js/parseFloat (if (nil-or-empty? value) "0" value))
            selection-start (dom/prop el "selectionStart")
            selection-end (dom/prop el "selectionEnd")
            value-maybe
              (cond
                (ikey/num-key? key-code) (as-> key-code $
                                              (ikey/to-num $)
                                              (str
                                                (subs value 0 selection-start)
                                                $
                                                (subs value selection-end))
                                              (js/parseFloat $))
                (and (ikey/minus? key-code)
                     (or
                       (nil? selection-start)
                       (= selection-start 0)))
                (str "-" value)

                (and (ikey/minus? key-code)
                     (and
                       (some? selection-start)
                       (not= selection-start 0)))
                (str value "-")

                (ikey/dot? key-code) (str value ".0")
                (arrow-up-with-alt? e) (arrow-up-with-alt num-value step)
                (arrow-down-with-alt? e) (arrow-down-with-alt num-value step)
                (arrow-up-with-shift? e) (arrow-up-with-shift num-value step)
                (arrow-down-with-shift? e) (arrow-down-with-shift num-value step)
                (ikey/arrow-up? key-code) (+ num-value step)
                (ikey/arrow-down? key-code) (- num-value step))
            matches (matcher (str value-maybe))
            final-value (if (some? matches)
                          (str (nth matches 1) (nth matches 2))
                          min-val)]
        (cond
          (overflow? final-value)
          (dom/prevent-default! e)

          (or (ikey/arrow-up? key-code)
              (ikey/arrow-down? key-code)
              (arrow-up-with-shift? e)
              (arrow-down-with-shift? e)
              (arrow-up-with-alt? e)
              (arrow-down-with-alt? e)
              (not (nil-or-empty? (nth matches 3))))
          (do
            (dom/prevent-default! e)
            (dom/prop! el "value" final-value)
            (dom/setSelectionRange el selection-start selection-start)))))))

(defn keyup-handler
  [evt]
  (let [e (dom/get-evt evt)
        el (dom/get-target e)
        key-code (dom/get-key-code e)]
    (if
      ;; skip when press delete, baskspace, arrow-left, arrow-right
      (or (ikey/delete? key-code)
          (ikey/backspace? key-code)
          (ikey/arrow-left? key-code)
          (ikey/arrow-right? key-code))
      true
      ;; check value
      (let [e (dom/get-evt evt)
            el (dom/get-target e)
            attr (partial dom/attr el)
            selection-start (dom/prop el "selectionStart")
            selection-end (dom/prop el "selectionEnd")
            min-val (attr "min")
            max-val (attr "max")
            overflow? (partial pred/overflow? min-val max-val)
            matcher (partial
                      re-matches
                      (as-> (attr "precision" js/Number.MAX_SAFE_INTEGER) $
                        (str "^(" (if (>= min-val 0) "" "[-]?") "[0-9]*)" (if (< 0 $) (str "(?:(\\.[0-9]{0," $ "}))?") "()") "(.*)$")
                        (js/RegExp (trace $))))
            value (dom/prop el "value" "")
            matches (matcher value)
            final-value (if (some? matches)
                          (str (nth matches 1) (nth matches 2))
                          min-val)]
        (dom/prop!
          el
          "value"
          (cond
            (pred/gt-max? max-val final-value) max-val
            (pred/lt-min? min-val final-value) min-val
            :else final-value))
        (dom/setSelectionRange el selection-start selection-start)))))

(defn ^:export init
  [el]
  (set! (.. el -style -imeMode) "disabled") ;; IE下禁用输入法
  (set! (.-type el) "text")                 ;; 为控制光标，需将type=number转换为type=text
  (dom/listen! el "keydown" keydown-handler)
  (dom/listen! el "keyup" keyup-handler))

;;(init (-> "input" dom/$ seq first))

;; 上限限制
;; 下限限制
;; 精度限制

;; 场景:非输入法, 拦截keydown作预判

;; 场景:输入法,拦截keyup作补救

;; 拦截例外, 0-9, ., delete, baskspace, arrow left, arrow right
;; 拦截符号, min为0时不允许输入负号
;; 拦截小数点, precision为0时不允许输入小数点
;; 附加，arrow-up -> (partial inc step), arrow-down -> (partial dec step)
;;       arrow-up 的默认行为是光标移动到行首
;;       arrow-down 的默认行为是光标移动到行尾
;;       shift + arrow-up -> (partial inc (* 10 step))
;;       shift + arrow-down -> (partial dec (* 10 step))
;;       ctrl + arrow-up -> (partial inc (* 0.1 step))
;;       ctrl + arrow-down -> (partial dec (* 0.1 step))

