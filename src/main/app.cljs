(ns main.app
  (:require
   ["react-dom/client" :as rdom]
   [helix.core :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [main.lib :refer [defnc]]))

;; app
(defnc app []
  (let [[_state _set-state] (hooks/use-state {})]
    (d/div
     (d/h1 "Blog test" )
     (d/h2 "a simple blog engine")
     (d/p "made in clojure"))))

;; start your app with your React renderer
(defn ^:export init []
  (doto (rdom/createRoot (js/document.getElementById "app"))
    (.render ($ app))))
