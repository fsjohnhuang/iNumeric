(require '[cljs.build.api :as b])

(println "Building ...")

(let [start (System/nanoTime)]
  (b/build "src/"
    {:main 'i-numeric.core
     :output-to "out/i_numeric.js"
     :output-dir "out/"
     :verbose true})
  (println "... done. Elapsed" (/ (- (System/nanoTime) start) 1e9) "seconds"))


