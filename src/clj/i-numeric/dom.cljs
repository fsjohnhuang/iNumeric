(ns i-numeric.dom)

(extend-type js/NodeList
  ISeqable
  (-seq [this]
    (let [l (.-length this)
          v (transient [])]
      (doseq [i (range l)]
        (->> i
          (aget this)
          (conj! v)))
      (persistent! v))))

(defn get-evt
  "获取EventObject实例"
  [e]
  (if (some? e) e js/window.event))

(defn get-key-code
  "从EventObject中获取keyCode"
  [e]
  (let [key-code (.-keyCode e)]
    (if (some? key-code) key-code (.-which e))))


(defn get-target
  "从EventObject实例获取源元素"
  [e]
  (let [target (.-target e)]
    (if (some? target) target (.-srcElement e))))

(defn attr
  "获取元素的attribute属性"
  [el name]
  (.getAttribute el (clj->js name)))

(defn attr!
  "设置元素的attribute属性"
  [el name val]
  (.setAttribute el (clj->js name) val))

(defn prop
  "获取元素的属性"
  [el name]
  (aget el (clj->js name)))

(defn prop!
  "设置元素的属性"
  [el name val]
  (aset el (clj->js name) val))

(defn prevent-default!
  "阻止默认行为"
  [e]
  (if (fn? (.-preventDefault e))
    (do
      (.preventDefault e)
      (set! (.-returnValue e) false))
    true))

(defn $
  "获取元素"
  ([slctr] (js/document.querySelectorAll slctr))
  ([slctr ctx] (.querySelectorAll ctx slctr)))
