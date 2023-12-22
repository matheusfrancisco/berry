(ns main.app
  (:require
   ["react-dom/client" :as rdom]
   [helix.core :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [main.api :refer [meu-print]]
   [main.lib :refer [defnc]]))

(defn replace-str [^String s]
  (.replace s "_" "-"))

(defn convert-keys [obj]
  (reduce-kv (fn [m k v]
               (assoc m (-> k
                            (name)
                            (replace-str)
                            keyword) v))
             {}
             obj))

(defn js->cljs-key [obj]
  (js->clj obj :keywordize-keys true))

(defnc app []
  (let [[_state _set-state] (hooks/use-state {})
        posts-list [{:title "Test" :date "2023-12-13"
                     :body "testestestestestesteststes"}]]
    (meu-print (clj->js {:a"test"}))
    (d/body
     (d/div {:class-name "site-header"}
            (d/body {:class-name "wrapper"}
                    (d/div {:class-name "site-nav"}
                           (d/a {:class-name "page-link" :href "#"} "Home")
                           (d/a {:class-name "page-link" :href "#"} "Posts")
                           (d/a {:class-name "page-link" :href "#"} "Archives")
                           (d/a {:class-name "page-link" :href "#"} "Tags")
                           (d/a {:class-name "page-link" :href "#"} "About"))

                    (d/div
                     (d/h1 {:class-name "site-title"}
                           (d/a {:href "#"} "My Blog"))
                     (d/p "Write ideas and study notes here."))))

     (d/div {:class-name "wrapper"}
            (for [{:keys [post-link
                          body
                          truncated
                          date
                          discuss
                          title]} posts-list]
              (d/div
               {:key title}
               (d/h1
                (d/a {:href post-link} title))
               body
               (when truncated
                 (d/p {} (d/a {:href post-link} "Continue reading")))
               (when discuss
                 (d/p {} (d/a {:href discuss} "Discuss this post here")))
               (d/p (d/i "Published: " (d/span {:class-name "post-date"} date)))))))))

;; start your app with your React renderer
(defn ^:export init []
  (doto (rdom/createRoot (js/document.getElementById "app"))
    (.render ($ app))))
