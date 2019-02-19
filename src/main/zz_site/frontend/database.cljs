(ns zz-site.frontend.database
  (:require
    [datascript.core :as d]
    [zz-site.frontend.saved-data :as saved]))


(defn response
  "Can synchronously get
  https://api.github.com/users/zz8481111/repos               1000 msecs
  https://api.github.com/repos/zz8481111/test-repo-1          600 msecs
  https://api.github.com/repos/zz8481111/test-repo-1/readme   400 msecs"
  [link]
  (let [request (js/XMLHttpRequest.)
        _ (.open request "GET" link false)
        _ (.send request)]

    (js->clj (js/JSON.parse (.-response request)))))

(def repo-small-queries #{"private" "updated_at" "url" "full_name" "html_url" "id"})
(def repo-full-queries  #{"git_commits_url"
                          "pushed_at"
                          "owner"
                          "license"
                          "url"
                          "contributors_url"
                          "trees_url"
                          "deployments_url"
                          "full_name"
                          "milestones_url"
                          "issue_comment_url"
                          "tags_url"
                          "node_id"
                          "blobs_url"
                          "stargazers_count"
                          "ssh_url"
                          "forks"
                          "has_pages"
                          "private"
                          "pulls_url"
                          "keys_url"
                          "id"
                          "homepage"
                          "has_projects"
                          "subscription_url"
                          "notifications_url"
                          "collaborators_url"
                          "contents_url"
                          "watchers"
                          "name"
                          "archived"
                          "compare_url"
                          "open_issues"
                          "stargazers_url"
                          "has_issues"
                          "updated_at"
                          "git_url"
                          "assignees_url"
                          "open_issues_count"
                          "commits_url"
                          "html_url"
                          "labels_url"
                          "git_refs_url"
                          "forks_count"
                          "issue_events_url"
                          "languages_url"
                          "downloads_url"
                          "mirror_url"
                          "comments_url"
                          "archive_url"
                          "events_url"
                          "watchers_count"
                          "hooks_url"
                          "created_at"
                          "teams_url"
                          "has_wiki"
                          "has_downloads"
                          "size"
                          "fork"
                          "subscribers_url"
                          "releases_url"
                          "language"
                          "branches_url"
                          "statuses_url"
                          "clone_url"
                          "forks_url"
                          "issues_url"
                          "description"
                          "default_branch"
                          "merges_url"
                          "git_tags_url"
                          "svn_url"})
(def gist-small-queries #{"public" "updated_at" "url" "html_url" "files" "description" "id"})
(def gist-full-queries #{"owner"
                         "truncated"
                         "url"
                         "node_id"
                         "user"
                         "git_pull_url"
                         "id"
                         "files"
                         "updated_at"
                         "commits_url"
                         "html_url"
                         "comments_url"
                         "comments"
                         "git_push_url"
                         "created_at"
                         "public"
                         "forks_url"
                         "description"})

(defn short-reps-map [user-repos]
  (->> user-repos
       (filter #(false? (% "private")))
       (map #(select-keys % repo-small-queries))
       (map #(zipmap (map
                       (fn [x] (keyword "repo" x))
                       (keys %1))
                     (vals %1)))))

(defn short-gists-map [user-gists]
  (->> user-gists
       (filter #(% "public"))
       (map #(select-keys % gist-small-queries))
       (map #(zipmap (map
                       (fn [x] (keyword "gist" x))
                       (keys %1))
                     (vals %1)))))



(def newdb-schema
  {:user/login       {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db
                      :db/unique             :db.unique/identity}

   :user/repos       {:db/cardinality        :db.cardinality/many
                      :db/valueType          :db.type/ref
                      :db/isComponent        true}

   :repo/full_name   {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db}

   :repo/private     {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db}

   :repo/html_url    {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db
                      :db/unique             :db.unique/identity}

   :repo/content     {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db}

   :repo/updated_at  {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db.install/_attribute :db.part/db}

   :repo/id          {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}

   :user/gists       {:db/cardinality        :db.cardinality/many
                      :db/valueType          :db.type/ref
                      :db/isComponent        true}

   :gist/name        {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}

   :gist/description {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}

   :gist/public      {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}

   :gist/html_url    {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)
                      :db/unique             :db.unique/identity}

   :gist/updated_at  {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}

   :gist/id          {:db/cardinality        :db.cardinality/one
                      :db/id                 (d/tempid :db.part/db)}})



(def newdb (d/create-conn newdb-schema))



(defn- load-user-work
  [[user work]]
  (cond
    (= work :repos)
    (do (doseq [x (saved/data [user work])]
          (d/transact! newdb [{:db/id           -1
                               :repo/id         (:repo/id x)
                               :repo/full_name  (:repo/full_name x)
                               :repo/private    (:repo/private x)
                               :repo/html_url   (:repo/html_url x)
                               :repo/updated_at (:repo/updated_at x)}
                              {:db/id -2
                               :user/login user
                               :user/repos -1}]))
        ())
    (= work :gists)
    (doseq [x (saved/data ["zz8481111" :gists])]
      (d/transact! newdb [{:db/id           -1
                           :gist/name  (-> x
                                           :gist/files
                                           keys
                                           first)
                           :gist/public      (:gist/public x)
                           :gist/html_url    (:gist/html_url x)
                           :gist/updated_at  (:gist/updated_at x)
                           :gist/description (:gist/description x  "no description")
                           :gist/id          (:gist/id x)}
                          {:db/id -2
                           :user/login user
                           :user/gists -1}]))
    :else nil))


(load-user-work ["zz8481111" :repos])
(load-user-work ["zz8481111" :gists])
(load-user-work ["clojure" :repos])








(defn user-gists-select-attr [user attr]
  (d/q '[:find ?gist-id ?val
         :in $ ?user ?attr
         :where
         [?u :user/login ?user]
         [?u :user/gists ?g]
         [?g ?attr ?val]
         [?g :gist/id ?gist-id]]
       @newdb user attr))


(defn gist-id-select-attrs [gist-id & attributes]  ; not work without all attributes in database
  (let [attrs      (cons :gist/id attributes)
        sym        (fn [x] (symbol (str "?" (name x))))
        selectors  (map sym attrs)
        gid        (gensym "?gist")
        rule       (fn [x-attr] [gid x-attr (sym x-attr)])
        rules      (map rule attrs)]
    (first (d/q `[:find ~@selectors
                  :in ~'$ ~'?id
                  :where ~@rules]
                @newdb gist-id))))


(defn user-repos-select-attr [user attr]
  (d/q '[:find ?repo-id ?val
         :in $ ?user ?attr
         :where
         [?u :user/login ?user]
         [?u :user/repos ?r]
         [?r ?attr ?val]
         [?r :repo/id ?repo-id]]
       @newdb user attr))


(defn repo-id-select-attrs [repo-id & attributes]  ; not work without all attributes in database
  (let [attrs      (cons :repo/id attributes)
        sym        (fn [x] (symbol (str "?" (name x))))
        selectors  (map sym attrs)
        rid        (gensym "?repo")
        rule       (fn [x-attr] [rid x-attr (sym x-attr)])
        rules      (map rule attrs)]
    (first (d/q `[:find ~@selectors
                  :in ~'$ ~'?id
                  :where ~@rules]
                @newdb repo-id))))


(defn get-datoms [] (d/seek-datoms @newdb :avet))


(defn query-github-user-repos [user]
  (let [local (d/q '[:find ?repo-id ?full_name
                     :in $ ?user
                     :where
                     [?u :user/login ?user]
                     [?u :user/repos ?r]
                     [?r :repo/full_name ?full_name]
                     [?r :repo/id ?repo-id]] @newdb user)]
    (if-not (empty? local)
      local
      (let [loaded (response (str "https://api.github.com/users/" user "/repos"))]
        (when-not (= "Not Found" (get loaded "message"))
          (doseq [x (short-reps-map loaded)]
            (d/transact! newdb [{:db/id           -1
                                 :repo/id         (:repo/id x)
                                 :repo/full_name  (:repo/full_name x)
                                 :repo/private    (:repo/private x)
                                 :repo/html_url   (:repo/html_url x)
                                 :repo/updated_at (:repo/updated_at x)}
                                {:db/id -2
                                 :user/login user
                                 :user/repos -1}]))
          (d/q '[:find ?repo-id ?full_name
                 :in $ ?user
                 :where
                 [?u :user/login ?user]
                 [?u :user/repos ?r]
                 [?r :repo/full_name ?full_name]
                 [?r :repo/id ?repo-id]] @newdb user))))))
