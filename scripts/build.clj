(require '[cljs.build.api :as b])

(println "Building ...")

(let [start (System/nanoTime)]
  (b/build "src/clj"
    {:main 'i-numeric.core
     :output-to "out/clj/inumeric.js"
     :output-dir "out/clj"
     :verbose true})
  (println "... done. Elapsed" (/ (- (System/nanoTime) start) 1e9) "seconds"))


