(ns main.app
  (:require
   ["react-router-dom" :as rrd]
   ["react" :as react]
   ["html-react-parser" :as parse]
   ["react-dom/client" :as rdom]
   [cljs.reader :as reader]
   [helix.core :refer [$]]
   [helix.dom :as d]
   [helix.hooks :as hooks]
   [main.lib :refer [defnc]]))

(defn js->cljs-key [obj]
  (js->clj obj :keywordize-keys true))

(def initial-posts {:posts []})

(defn posts-reducer [state action]
  (case (:type action)
    :added {:posts (concat (:items state) (:items action))}
    :default (-> (js/Error. (str "Unknown action: " (:type action)))
                 (throw))))

(def task-context (react/createContext nil))
(def task-dispatch-context (react/createContext nil))

(defnc nav []
  (d/div {:class-name "site-header"}
         (d/div {:class-name "wrapper"}

                (d/div {:class-name "site-nav"}
                       ($ rrd/Link {:to "/"} (d/p {:class-name "page-link"} "Home"))
                       ($ rrd/Link {:to "/posts"} (d/p {:class-name "page-link"} "Posts"))
                       ($ rrd/Link {:to "/tags"} (d/p {:class-name "page-link"} "Tags"))
                       ($ rrd/Link {:to "https://github.com/matheusfrancisco/matheusfrancisco"} (d/p {:class-name "page-link"} "About")))

                (d/div
                 (d/h1 {:class-name "site-title"}
                       ($ rrd/Link {:to "/"} "My Blog"))
                 (d/p "Write ideas and study notes here.")))))

(defnc tags []
  (d/div "Building..."))

(defnc home []
  (let [{:keys [posts]}  (hooks/use-context task-context)]

    (d/div {:class-name "wrapper"}
           (for [{:keys [post-link
                         body
                         truncated
                         date
                         id
                         discuss
                         title]} posts]
             (d/div
              {:key title}
              (d/h1
               ($ rrd/Link {:to (str "posts/" id)} title))
              (when body (parse/default body))
              (when truncated
                (d/p {} (d/a {:href post-link} "Continue reading")))
              (when discuss
                (d/p {} (d/a {:href discuss} "Discuss this post here")))
              (d/p (d/i "Published: " (d/span {:class-name "post-date"} date))))))))

(defnc post-body []
  (d/div
   ($ rrd/Outlet)))

(defnc post []
  (let [{:keys [id]} (js->cljs-key (rrd/useParams))
        {:keys [posts]}  (hooks/use-context task-context)
        {:keys [body date id title]} (first (filter #(= (str (:id %)) id) posts))]

    (prn posts id)
    (d/div
     {:class-name "wrapper"}
     (d/div
      {:key title}
      (d/h1
       title)
      (parse/default body)
      (d/p (d/i "Published: " (d/span {:class-name "post-date"} date)))))))

(defnc postlist []
  (let [{:keys [posts]}  (hooks/use-context task-context)]
    (d/div {:class-name "wrapper"}
           (for [{:keys [date
                         id
                         title]} posts]
             (d/div
              {:key title}
              (d/h1
               ($ rrd/Link {:to (str "/posts/" id)} title))
              (d/div "...")
              (d/p (d/i "Published: " (d/span {:class-name "post-date"} date))))))))

(defnc app []
  (let [[infos set-infos] (hooks/use-state [])
        [posts dispatch] (hooks/use-reducer posts-reducer initial-posts)]

    (hooks/use-effect
      :once
      (-> (js/fetch "./posts/posts.edn")
          (.then (fn [r] (.text r)))
          (.then (fn [r] (reader/read-string r)))
          (.then (fn [r]
                   (set-infos concat r)))))

    (hooks/use-effect
      [infos]
      (when (seq infos)
        (-> infos
            (->>
             (map (fn [info] (-> (js/fetch (:href info))
                                 (.then (fn [r] (.text r)))
                                 (.then (fn [r]
                                          (assoc info :body r))))))
             js/Promise.all)
            (.then (fn [i]
                     (let [items (mapv js->cljs-key i)]
                       (dispatch {:type :added
                                  :items items})))))))

    (helix.core/provider
     {:context task-dispatch-context
      :value dispatch}
     (helix.core/provider
      {:context task-context
       :value posts}
      ($ rrd/BrowserRouter
         ($ nav)
         ($ rrd/Routes
            ($ rrd/Route {:path "/" :element ($ home)})
            ($ rrd/Route {:path "/posts" :element ($ post-body)}
               ($ rrd/Route {:index true :element ($ postlist)})
               ($ rrd/Route {:path ":id" :element ($ post)}))

            ($ rrd/Route {:path "/tags" :element ($ tags)})))))))

;; start your app with your React renderer
(defn ^:export init []
  (doto (rdom/createRoot (js/document.getElementById "app"))
    (.render ($ app))))
