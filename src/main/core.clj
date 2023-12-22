(ns main.core 
  (:require
   [main.api :refer [meu-print]]))

(defn -main
  [& args]
  (meu-print "Hello, World!")
  (println "Hello, World!"))
