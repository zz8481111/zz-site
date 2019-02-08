(ns zz-site.snippets
  (:require
    [rum.core :as rum]
    [zz-site.util :as util]
    [bidi.bidi :as bidi]))




;;
;;(rum/defc blog-sidebar [model]       ; !! used @model
;;  [:div#sidebar
;;   [:h1#logo [:a {:on-click (fn [_]
;;                              (js/alert
;;                                "OK!"))}
;;              "STRIPED"]] ;
;;   [:nav#nav
;;    [:ul#local_links
;;     (for [[k v] {"vals B"    :b-items                      ;"current db" {:model :database :collection [@database]}
;;                  "articles"  :articles
;;                  "clojure 3" :clojure-3
;;                  "clojure 7" :clojure-7
;;                  "numbers"   :numbers
;;                  "gists"     :gists}]
;;      [:li {:key k
;;            :class (when (= k (model :name)) "current")}
;;            ;:on-click (fn [_] (reset! app-state v))}
;;
;;       [:a k]])]]
;;   [:section.box.search
;;    [:form {:method :get} [:input.text {:type "text" :name "search" :placeholder "Search"}]]]
;;   [:section.box.text-style1
;;    [:div.inner
;;     [:p
;;      [:strong "Striped: "]
;;      "A free and fully responsive HTML5 site
;;          template designed by " [:a {:href "http://twitter.com/ajlkn"} "AJ"]
;;      " for " [:a {:href "http://html5up.net/"} "HTML5 UP"]]]]
;;   [:section.box.recent-posts
;;    [:header [:h2 "Recent Posts"]]
;;    [:ul (for [x '("Lorem ipsum dolor"
;;                   "Feugiat nisl aliquam"
;;                   "Sed dolore magna"
;;                   "Malesuada commodo"
;;                   "Ipsum metus nullam")]
;;          [:li {:id x} [:a {:href "#"} x]])]]
;;   [:ul#copyright
;;    [:li#titled "\u00A9 Untitled."]
;;    [:li#design "Design: " [:a {:href "http://html5up.net"} "HTML5 UP"]]]])



;;(rum/defc post
;;  [{:keys [title subtitle info image-featured content]}]
;;  [:article.box.post.post-excerpt
;;   [:header
;;    [:h2 title]
;;    [:p [:button {:on-click (fn [_] '(show-first-from-page))} subtitle]]]
;;   [:div.info info]
;;   [:pre  content]
;;   [:a.featured image-featured]])


;;(rum/defc blog-title-bar [_]
;;  [:div#titleBar
;;   [:a.toggle {:href "#sidebar" :on-click (fn [_] '(toggle-sidebar))}]
;;   [:span.title [:a {:href "#"} "STRIPED"]]])





;;(rum/defc pagination-article [model]
;;  [:div.pagination
;;   [:button.w15
;;    (-> {:on-click (fn [_] '(show-prev))}
;;        (cond-> (zero? (:nth model)) (assoc :disabled true)))
;;    "Prev"]
;;   [:button.w15 {:on-click (fn [_] '(show-list))} "List"]
;;   [:button.w60
;;    (-> {:on-click (fn [_] '(show-next))}
;;        (cond-> (= (dec (count (:collection model))) (:nth model))
;;              (assoc :disabled true)))
;;    "Next"]])


;;(rum/defc pagination-page [model]
;;  [:div.pagination
;;   [:button.first.lil
;;    (-> {:on-click (fn [_] '(show-first))}
;;        (cond-> (< (:nth model) (:items-on-page model))
;;              (assoc :disabled true)))
;;    "<<"]
;;   (for [n (util/need-buttons-fn (util/page-fn model)
;;                               (util/count-pages-fn model))]
;;    [:button.lil {:on-click (fn [_] '(show-number n))
;;                  :number   n
;;                  :active (util/active?-yes-no n model)} n])
;;   [:button.last.lil {:on-click (fn [_] '(show-last-page))
;;                      :active (util/active?-yes-no (js/Math.ceil (/ (count (:collection model)) (:items-on-page model))) model)
;;                      :number   (js/Math.ceil (/ (count (:collection model)) (:items-on-page model)))}
;;
;;    (str (js/Math.ceil (/ (count (:collection model)) (:items-on-page model))))]
;;   [:button.next
;;    (-> {:on-click (fn [_] '(show-next-page))}
;;        (cond-> (>= (:nth model) (- (count (:collection model))
;;                                    (:items-on-page model)))
;;              (assoc :disabled true)))
;;    "Next"]])



;;(rum/defc blog-default [_]
;;  [:div.inner
;;   [:article.box.post.post-excerpt
;;    [:header [:h2 "404"]]]])


;;(rum/defc blog-article [{:keys [collection nth] :as model}]
;;  [:div.inner
;;   (post (get collection nth))
;;   (pagination-article model)])

;;(rum/defc blog-page [model]
;;  [:div.inner
;;   [:div (pagination-page model)]
;;   [:div (mapv post (let [n (:nth model)
;;                          it (:items-on-page model)
;;                          all (count (:collection model))]
;;                      (subvec (:collection model) (- n (mod n it)) (min
;;                                                                     (- n (mod n it) (- it))
;;                                                                     (count (:collection model))))))]
;;   [:div (pagination-page model)]])


;;(rum/defc blog-single-item [model]
;;  [:div.inner
;;   (post (first (:collection model)))])


;;(rum/defc blog-basic [model]
;; [:div#wrapper
;;  (cond
;;    (empty? (:collection model)) [:div#content (blog-default model)]
;;    (= :article (:model model)) [:div#content (blog-article model)]
;;    (= :page (:model model)) [:div#content (blog-page model)])
;;    ;(= :database (:model model)) [:div#content (blog-single-item model)]
;;
;;  (blog-sidebar model)
;;  (blog-title-bar model)])

