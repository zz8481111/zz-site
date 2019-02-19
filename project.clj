(defproject zz-site "0.1.0-SNAPSHOT"
  :description "JavaScript generator for https://zz8481111.github.io/"
  :url "https://github.com/zz8481111/zz-site"

  :min-lein-version "2.8.3"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 #_[org.clojure/core.async "0.3.443"
                    :exclusions [org.clojure/tools.reader]]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [bidi "2.1.5"]
                 [venantius/accountant "0.2.4"]
                 [pez/clerk "1.0.0"]
                 [rum "0.11.3"]
                 [datascript "0.17.1"]
                 [org.clojure/core.async  "0.4.490"]
                 [markdown-clj "1.0.7"]]

  :plugins [[lein-figwheel "0.5.16"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[prismatic/schema "1.1.7"]
                                  [figwheel-sidecar "0.5.4-6"]]}}


  :cljsbuild {:builds
              {:dev
               {:source-paths ["src" "test"]

                :figwheel {:on-jsload      "zz-site.core/on-js-reload"
                           :websocket-host :js-client-host}


                :compiler {:main zz-site.core
                           :asset-path "/js/compiled/out"
                           :output-to "resources/public/js/compiled/zz_site.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               :min
               {:source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/zz_site.js"
                           :main zz-site.core
                           :optimizations :advanced
                           :pretty-print false}}}}

  :figwheel {:http-server-root "public"
             :server-port 4449
             :server-ip "0.0.0.0"
             :css-dirs ["resources/public/css"]
             :ring-handler zz-site.server/handler}

  :repl-options {:init-ns zz-site.user
                 :skip-default-init false
                 :nrepl-middleware [cider.piggieback/wrap-cljs-repl]})
