(ns tasks
  "Functions used in bb.edn"
  (:require
   #_[babashka.deps :as deps]
   [babashka.fs :as fs]
   [markdown.core :as md]
   #_[babashka.tasks :refer [shell]]
   [clojure.string :as str]))

(defn build-posts []
  (prn "building posts... ")
  (when (fs/exists? "public/posts")
    (fs/delete-tree "public/posts"))
  (fs/create-dir "public/posts")
  (let [posts (fs/list-dir "posts")
        metainfo (atom [])
        post-info (map (fn [f]
                         (let [m (->
                                  (slurp (fs/file f))
                                  (md/md-to-html-string-with-meta :reference-links? true
                                                                  :heading-anchors true
                                                                  :footnotes? true
                                                                  :code-style
                                                                  (fn [lang]
                                                                    (format "class=\"lang-%s\"" lang))))]
                           (merge {:filename (str/replace (fs/file-name f) ".md" ".html")
                                   :href (str/replace (fs/file-name f) " .md " " .html ")}
                                  m)))
                       posts)
        create-meta-posts (fn [{:keys [filename html metadata]}]
                            (let [id (random-uuid)
                                  {:keys [title date tags]} metadata
                                  info {:title title
                                        :id (random-uuid)
                                        :date date
                                        :tags tags
                                        :href (str "./posts/" filename)}]
                              info))

        create-post (fn [{:keys [filename html]}]
                      (let [path (str "public/posts/" filename)]
                        (fs/create-file path)
                        (spit path html)))]

    (doseq [p post-info]
      (create-post p)
      (swap! metainfo conj (create-meta-posts p)))
    (fs/create-file "public/posts/posts.edn")
    (spit "public/posts/posts.edn" @metainfo)))

(comment

  (build-posts)
;
  )
