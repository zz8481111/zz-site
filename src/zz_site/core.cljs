(ns zz-site.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [rum.core :as rum]
            [bidi.bidi :as bidi]
            [cljs.pprint :as pp]
    ;;[schema.core :as s] ;For when defining routes get tricky
    ;;[bidi.schema]
            [zz-site.util :as util]
            [zz-site.database :as db]
            [accountant.core :as accountant]
            [clerk.core :as clerk])
  (:import
    [goog.history.Html5History]))


(enable-console-print!)



;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ROUTES
;;;;;;;;;;;;;;;;;;;;;;;;;;



(def inner-route (atom []))

(def app-routes
  ["/" {""              :index
        "index"         :index
        "database"      :database
        "a-items"       {""                  :a-items
                         ["/item-" :item-id] :a-item}
        "numbers"       {["-page" :ids]     :numbers/page
                         ["-item" :item-id] :numbers/item}
        "b-items"       {""                  :b-items
                         ["/item-" :item-id] :b-item}
        "missing-route" :missing-route
        "clj-repo"      {["s-" :ids]          :clojure/page
                         ["-"  :item-id] :clojure/item}
        "zz-gist"       {["s-" :ids]     :zz.gists/page
                         ["-"  :item-id] :zz.gists/item}
        "zz-repo"     {["s-" :ids]     :zz/page
                       ["-"  :item-id] :zz/item}}])
; true            :four-o-four}])

;;(s/check bidi.schema/RoutePair app-routes)
;;(s/validate bidi.schema/RoutePair app-routes)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SNIPPETS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(rum/defc non-exist
  ([]
   [:div.inner
    [:article.box.post.post-excerpt
     [:header [:h2 "404: It is not here"]]
     [:pre.verse
      "What you are looking for,
I do not have.
How could I have,
what does not exist?"]]])
  ([message]
   [:div.inner
    [:article.box.post.post-excerpt
     [:header [:h2 "404: It is not here"]]
     [:pre.verse (str message)]]]))


(rum/defc a-items []
  [:div.inner
   [:article.box.post.post-excerpt
    [:h1 "The Lot of A Items"]
    [:div#red {:style {:width "50%" :height "200px" :background-color "red"}}]
    [:ul
     (map (fn [item-id]
            [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
             [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "A-item: " item-id]])
          (range 1 42))]
    [:div {:style {:width "50%" :height "200px" :background-color "green"}}]
    [:div#b-item-100-link [:a {:href (str (bidi/path-for app-routes :b-items) "#item-50")} "B-item: 50"]]
    [:div {:style {:width "50%" :height "200px" :background-color "blue"}}]
    [:ul
     (map (fn [item-id]
            [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
             [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "A-item: " item-id]])
          (range 42 78))]
    [:div {:style {:width "50%" :height "200px" :background-color "yellow"}}]
    [:p [:a {:href (bidi/path-for app-routes :b-items)} "Top of b-items"]]]])



(rum/defc blog-sidebar []
  [:div#sidebar
   [:h1#logo [:a {:on-click (fn [_] (js/alert (session/get :route)))} "STRIPED"]] ;  ! delete this
   [:nav#nav
    [:ul#local_links
     (for [[k v] (sorted-map "home" (bidi/path-for app-routes :index)
                             "database" (bidi/path-for app-routes :database)
                             "a-10" (bidi/path-for app-routes :a-item :item-id "10")
                             ; "bottom" "#bottom"
                             ; "top" "#top"
                             "vals B" (bidi/path-for app-routes :b-items)
                             "articles" (bidi/path-for app-routes :zz/page :ids "1-10")
                             "clj-repos" (bidi/path-for app-routes :clojure/page :ids "1-10")
                             "numbers" (bidi/path-for app-routes :numbers/page :ids "1-10")
                             "gists" (bidi/path-for app-routes :zz.gists/page :ids "1-2"))]
       [:li {:key   k
             :class (when (= (keyword k) (:current-page (session/get :route))) "current")} [:a {:href v} k]])]]
   [:section.box.search
    [:form {:method :get} [:input.text {:type "text" :name "search" :placeholder "Search"}]]]
   [:section.box.text-style1
    [:div.inner
     [:p
      [:strong "Striped: "]
      "A free and fully responsive HTML5 site
          template designed by " [:a {:href "http://twitter.com/ajlkn"} "AJ"]
      " for " [:a {:href "http://html5up.net/"} "HTML5 UP"]]]]
   [:section.box.recent-posts
    [:header [:h2 "Recent Posts"]]
    [:ul (for [x '("Lorem ipsum dolor"
                    "Feugiat nisl aliquam"
                    "Sed dolore magna"
                    "Malesuada commodo"
                    "Ipsum metus nullam")]
           [:li {:id x "key" x} [:a {:href "#"} x]])]]
   [:ul#copyright
    [:li#titled "\u00A9 Untitled."]
    [:li#design "Design: " [:a {:href "http://html5up.net"} "HTML5 UP"]]]])



(rum/defc post
  [{:keys [title subtitle info image-featured content]} route-ns nkey]
  [:article.box.post.post-excerpt {"key" title}
   [:header
    [:h2 title]
    [:p [:a {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id title)} subtitle]]]
   [:div.info info]
   [:pre content]
   [:a.featured image-featured]])





(rum/defc blog-title-bar []
  [:div#titleBar
   [:a.toggle {:href "#sidebar" :on-click (fn [_] (util/toggle-sidebar))}]
   [:span.title [:a {:href "#"} "STRIPED"]]])





(rum/defc pagination-article' [coll-size k route-ns]
  [:div.pagination
   (if (zero? k)
     [:a.button.disabled "Prev"]
     [:a.button {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id k)} "Prev"])
   [:a.button {:href (bidi/path-for app-routes (keyword route-ns "page") :ids (str (inc k) "-" (+ 10 k)))} "List"]
   (if (= (dec coll-size) k)
     [:a.button.disabled "Next"]
     [:a.button {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id (+ 2 k))} "Next"])])

(rum/defc pagination-article [coll-size coll k route-ns]
  [:div.pagination
   (if (zero? k)
     [:a.button.disabled "Prev"]
     [:a.button {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id (first (coll (dec k))))} "Prev"])
   [:a.button {:href (bidi/path-for app-routes (keyword route-ns "page") :ids "1-10")} "List"]
   (if (= (dec coll-size) k)
     [:a.button.disabled "Next"]
     [:a.button {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id (first (coll (inc k))))} "Next"])])


;; OK
(rum/defc pagination-page [coll-size from to route]
  "Generate pagination block with buttons
  from, to - indexes inclusive
  "
  (let [items-on-page (inc (- to from))
        pages         (js/Math.ceil (/ coll-size items-on-page))
        cur-page      (inc (quot from items-on-page))]
    [:div.pagination
     ;; First button
     [:a.button {:href (bidi/path-for app-routes route :ids (str "1-" items-on-page))
                 "active" (str (= cur-page 1))} "<<"] ;;
     ;; Adjacent
     ;; Last
     (for [x (util/need-buttons-fn cur-page pages)]
       [:a.button {:href    (bidi/path-for app-routes route :ids (#(str %1 "-" (+ (- to from) %1)) (inc (* (dec x) (inc (- to from))))))
                   "active" (str (= cur-page x))
                   :key     (or x -1)} x])
     ;; Next
     (if (< to (dec coll-size))
       [:a.button {:href (bidi/path-for app-routes
                                        route
                                        :ids (str (+ 2 to)
                                                  "-"
                                                  (+ 2 to (- to from))))} "Next"]
       [:a.button.disabled "Next"])]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PAGE CONTENTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti page-contents identity)


(defmethod page-contents :index []
  (fn []
    [:span.main
     [:h1 "Welcome to zz-site"]
     [:ul
      [:li [:a {:href (bidi/path-for app-routes :a-items)} "Lots of items of type A"]]
      [:li [:a {:href (bidi/path-for app-routes :b-items)} "Many items of type B"]]
      [:li [:a {:href (bidi/path-for app-routes :missing-route)} "A Missing Route"]]
      [:li [:a {:href (bidi/path-for app-routes :app-state)} "A state"]]
      [:li [:a {:href "/broken/link"} "A Broken Link"]]]
     [:p "Using "
      [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
      [:a {:href "https://github.com/juxt/bidi"} "Bidi"] ", "
      [:a {:href "https://github.com/venantius/accountant"} "Accountant"] " & "
      [:a {:href "https://github.com/PEZ/clerk"} "Clerk"]
      ". Find this example on " [:a {:href "https://github.com/PEZ/reagent-bidi-accountant-example"} "Github"]]]))


(defmethod page-contents :database []
  [:div.inner
   [:article.box.post.post-excerpt
    [:h2 "Database -avet"]
    (for [[i x] (map-indexed (fn [i x] [i x]) (db/get-datoms))]
      [:div {:key i} (with-out-str (pp/pprint x))])]])


(defmethod page-contents :a-items []
  (a-items))



(defmethod page-contents :b-items []
  (fn []
    [:span.main
     [:h1 "The Many B Items"]
     [:ul (map (fn [item-id]
                 [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "B-item: " item-id]])
               (range 1 117))]
     [:p [:a {:href (bidi/path-for app-routes :a-items)} "Top of a-items"]]]))



(defmethod page-contents :a-item []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of A")]
       [:p [:a {:href (bidi/path-for app-routes :a-items)} "Back to the list of A-items"]]])))


(defmethod page-contents :b-item []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of B")]
       [:p [:a {:href (bidi/path-for app-routes :items)} "Back to the list of B-items"]]])))


(defmethod page-contents :about []
  (fn [] [:span.main
          [:h1 "About zz-site"]]))


(defmethod page-contents :four-o-four []
  (non-exist))



(defmethod page-contents :clojure/page []
  (let [routing-data  (session/get :route)
        current-page  (get-in routing-data [:current-page])
        indexes       (get-in routing-data [:route-params :ids])
        [fst lst]     (map (comp dec js/parseInt) (rest (re-find #"(\d+)-(\d+)" indexes)))
        user-repos   (vec (sort-by second (db/user-repos-select-attr "clojure" :repo/full_name)))
        size          (count user-repos)
        on-page-repos (subvec user-repos fst (min size (inc lst)))]
    [:div.inner
     (pagination-page size fst lst current-page)
     (for [repo on-page-repos]
       (let [[id name] repo
             [_ description url info] (db/repo-id-select-attrs id
                                                               :repo/full_name
                                                               :repo/html_url
                                                               :repo/updated_at)]
         [:article.box.post.post-excerpt {:key id}
          [:header
           [:h2 name]
           [:p (if (clojure.string/blank? description) "no description" description)]
           [:a {:href (bidi/path-for app-routes (keyword (namespace current-page) "item") :item-id id)} "open"]
           [:p (str url)]]
          [:div.info (replace {"-" " "} (re-find #"\d+-\d+-\d+" info))]]))
     (pagination-page size fst lst current-page)]))


(defmethod page-contents :zz/page []
  (let [routing-data  (session/get :route)
        current-page  (get-in routing-data [:current-page])
        indexes       (get-in routing-data [:route-params :ids])
        [fst lst]     (map (comp dec js/parseInt) (rest (re-find #"(\d+)-(\d+)" indexes)))
        user-repos   (vec (sort-by second (db/user-repos-select-attr "zz8481111" :repo/full_name)))
        size          (count user-repos)
        on-page-repos (subvec user-repos fst (min size (inc lst)))]
    [:div.inner
     (pagination-page size fst lst current-page)
     (for [repo on-page-repos]
       (let [[id name] repo
             [_ description url info] (db/repo-id-select-attrs id
                                                               :repo/full_name
                                                               :repo/html_url
                                                               :repo/updated_at)]
         [:article.box.post.post-excerpt {:key id}
          [:header
           [:h2 name]
           [:p (if (clojure.string/blank? description) "no description" description)]
           [:a {:href (bidi/path-for app-routes (keyword (namespace current-page) "item") :item-id id)} "open"]
           [:p (str url)]]
          [:div.info (replace {"-" " "} (re-find #"\d+-\d+-\d+" info))]]))
     (pagination-page size fst lst current-page)]))


(def numbers (mapv (fn [x] {:title    x
                            :subtitle x
                            :info     (str (js/Date.))
                            :content  x}) (range 1 38)))

(defmethod page-contents :numbers/page []
  "Testing page with small data"
  (let [routing-data (session/get :route)
        indexes (get-in routing-data [:route-params :ids])
        [fst lst]     (map (comp dec js/parseInt) (rest (re-find #"(\d+)-(\d+)" indexes)))
        size (count numbers)]
    [:div.inner
     (pagination-page size fst lst :numbers/page)
     [:article.box.post.post-excerpt
      [:header [:h2 (str "items " fst "----" lst)]]]
     (for [x (subvec numbers fst (min size (inc lst)))]
       (post x (namespace :numbers/page) x))
     (pagination-page size fst lst :numbers/page)]))


(defmethod page-contents :numbers/item []
  (let [routing-data (session/get :route)
        item (get-in routing-data [:route-params :item-id])]
    [:div.inner
     (post (get-in  numbers [ (dec (js/parseInt item))]) (namespace :numbers/item) (js/parseInt item))
     (pagination-article' (count numbers) (dec (js/parseInt item)) (namespace :numbers/item))
     [:div#disqus_tread "disqus"]]))
;;#_(let [config {:page-url "zz8481111"
;;                :page-id  item}
;;        d js/document]
;;    (-> (.createElement d "script")))
;;#_[:script "var disqus_config = function () {this.page.url = 'zz8481111'; this.page.identifier = 1};
;;    (function() {var d = document, s = d.createElement('script');
;;                 s.src = 'https://zz8481111.disqus.com/embed.js';
;;                 s.setAttribute('data-timestamp', +new Date());
;;                 (d.head || d.body).appendChild(s)})();"]


(defmethod page-contents :clojure/item []
  (let [routing-data                    (session/get :route)
        item                            (js/parseInt (get-in routing-data [:route-params :item-id]))
        [_ name description updated-at] (db/repo-id-select-attrs item :repo/full_name :repo/html_url :repo/updated_at)
        user-repos                      (vec (sort-by second (db/user-repos-select-attr "clojure" :repo/full_name)))
        size                            (count user-repos)
        route-ns                        (namespace :clojure/item)
        k                               (.indexOf (map first user-repos) item)]
    [:div.inner
     [:article.box.post.post-excerpt {"key" item}
      [:header
       [:h2 name]
       [:p [:a {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id item)} (if (clojure.string/blank? description)
                                                                                            "no description" description)]]]

      [:pre "content"]
      [:a.featured nil]]
     (pagination-article size user-repos k (namespace :clojure/item))
     [:div#disqus_tread "disqus"]]))
;#_[:div#disqus_tread "disqus"]))

(defmethod page-contents :zz/item []
  (let [routing-data                    (session/get :route)
        item                            (js/parseInt (get-in routing-data [:route-params :item-id]))
        [_ name description updated-at] (db/repo-id-select-attrs item :repo/full_name :repo/html_url :repo/updated_at)
        user-repos                      (vec (sort-by second (db/user-repos-select-attr "zz8481111" :repo/full_name)))
        size                            (count user-repos)
        route-ns                        (namespace :zz/item)
        k                               (.indexOf (map first user-repos) item)]
    [:div.inner
     [:article.box.post.post-excerpt {"key" item}
      [:header
       [:h2 name]
       [:p [:a {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id item)} (if (clojure.string/blank? description)
                                                                                            "no description" description)]]]

      [:pre "content"]
      [:a.featured nil]]
     (pagination-article size user-repos k (namespace :zz/item))
     [:div#disqus_tread "disqus"]]))
;#_[:div#disqus_tread "disqus"]))


(defmethod page-contents :zz.gists/page []
  (let [routing-data  (session/get :route)
        current-page  (get-in routing-data [:current-page])
        indexes       (get-in routing-data [:route-params :ids])
        [fst lst]     (map (comp dec js/parseInt) (rest (re-find #"(\d+)-(\d+)" indexes)))
        user-gists    (vec (sort-by second (db/user-gists-select-attr "zz8481111" :gist/name)))
        size          (count user-gists)
        on-page-gists (subvec user-gists fst (min size (inc lst)))]
    [:div.inner
     (pagination-page size fst lst current-page)
     (for [gist on-page-gists]
       (let [[id name] gist
             [_ description url info] (db/gist-id-select-attrs id
                                                               :gist/description
                                                               :gist/html_url
                                                               :gist/updated_at)]
         [:article.box.post.post-excerpt {:key id}
          [:header
           [:h2 name]
           [:p (if (clojure.string/blank? description) "no description" description)]
           [:a {:href (bidi/path-for app-routes (keyword (namespace current-page) "item") :item-id id)} "open"]
           [:p (str url)]]
          [:div.info (replace {"-" " "} (re-find #"\d+-\d+-\d+" info))]]))
     (pagination-page size fst lst current-page)]))


(defmethod page-contents :zz.gists/item []
  (let [routing-data                    (session/get :route)
        item                            (get-in routing-data [:route-params :item-id])
        [_ name description updated-at] (db/gist-id-select-attrs item :gist/name :gist/description :gist/updated_at)
        user-gists                      (vec (sort-by second (db/user-gists-select-attr "zz8481111" :gist/name)))
        size                            (count user-gists)
        route-ns                        (namespace :zz.gists/item)
        k                               (.indexOf (map first user-gists) item)]
    [:div.inner
     [:article.box.post.post-excerpt {"key" item}
      [:header
       [:h2 name]
       [:p [:a {:href (bidi/path-for app-routes (keyword route-ns "item") :item-id item)} (if (clojure.string/blank? description)
                                                                                            "no description" description)]]]
      [:p (println-str user-gists)]
      [:p (str item " " k)]
      [:div.info updated-at]
      [:pre "content"]
      [:a.featured nil]]
     (pagination-article size user-gists k (namespace :zz.gists/item))]))
     ;#_[:div#disqus_tread "disqus"]))


(defmethod page-contents :default []
  (non-exist))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generate page
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div#wrapper
       [:div#content
        ^{:key page} [page-contents page]]
       (blog-sidebar)
       (blog-title-bar)])))


(defn on-js-reload []
  (reagent/render-component [current-page]
                            (. js/document (getElementById "app"))))

(defn ^:export init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
    {:nav-handler  (fn
                     [path]
                     (reagent/after-render clerk/after-render!)
                     (let [match (bidi/match-route app-routes path)
                           current-page (:handler match)
                           route-params (:route-params match)]
                       (session/put! :route {:current-page current-page
                                             :route-params route-params}))
                     (clerk/navigate-page! path))
     :path-exists? (fn [path]
                     (boolean (bidi/match-route app-routes path)))})
  (accountant/dispatch-current!)
  (on-js-reload))


;;;;;;;;;;;;;;
; Comments
;;;;;;;;;;;;;;

;;    (start)   - for start cljs repl on http://localhost:4449
