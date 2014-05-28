(defproject hackerfews "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [enlive "1.1.4"]
                 [org.bodil/cljs-noderepl "0.1.11"]
                 [com.cemerick/piggieback "0.1.3"]]
  :plugins [[cider/cider-nrepl "0.7.0-SNAPSHOT"]
            [lein-cljsbuild "1.0.3"]
            [lein-npm "0.4.0"]]

  :node-dependencies [[libxmljs "0.9.0"]
                      [request "2.36.0"]]


  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :main "js/main.js"

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                ; :externs ["node_modules/cheerio/lib/cheerio.js"]
                :optimizations :simple
                :output-to "js/main.js"
                :output-dir "js/out"
                :target :nodejs
                :source-map "js/main.js.map"}}]})
