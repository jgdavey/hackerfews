(ns hackerfews.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core :as cljs]
            [cljs.nodejs :as node]
            [goog.net.XhrIo :as xhr]
            [cljs.core.async :refer [put! chan <! >! close!] :as async]))

(enable-console-print!)

(def url "https://news.ycombinator.com/")

(def request (node/require "request"))
(def xml (node/require "libxmljs"))

(defn GET
  "returns a channel with a single string value"
  [url]
  (let [result (chan)
        on-result (fn [error response body]
                    (if error
                      (println "ERROR: " error)
                      (put! result (str body))))]

    (request url on-result)

    result))

(defn quality? [article]
  (< (:comments article) (:points article)))

(defn format-article [article]
  (str (:title article) "\n" (:url article) "\n"))

(defn get-number [string]
  (js/parseInt
    (or (re-find #"\d+" string) "0")))

(defn extract-article [raw-article]
  (let [a (.prevElement raw-article)
        subtexts (.. a parent parent nextElement
                     (get ".//td[contains(@class, 'subtext')]"))
        points (.get subtexts ".//span")
        comments (.get subtexts ".//a[last()]")]
    (when (and points comments)
      #js {:title (str (.text a))
           :url (.value (.attr a "href"))
           :points (get-number (.text points))
           :comments (get-number (.text comments)) })))

(defn extract-articles [raw]
  (js->clj (.map raw extract-article) :keywordize-keys true))

(defn ^:export main []
  (go
    (when-let [html (<! (GET url))]
      (let [doc (.parseHtmlString xml html)
            raw (.find doc "//span[contains(@class, 'comhead')]")
            articles (extract-articles raw)]
        (doseq [article (take 10 (filter quality? articles))]
          (println (format-article article)))))))

(set! *main-cli-fn* main)
(aset js/exports "core" hackerfews.core)
