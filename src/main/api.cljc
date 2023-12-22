(ns main.api)

#?(:cljs (defn meu-print [x] 
           (js/console.log "meu print in cljs")
           (js/console.log x)))

#?(:clj (defn meu-print [x] 
          (prn "clj")
          (println x)))
