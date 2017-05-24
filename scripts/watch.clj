(require '[cljs.build.api :as b])

(b/watch "src/"
  {:main 'i-numeric.core
   :output-to "out/i_numeric.js"
   :output-dir "out/"})
