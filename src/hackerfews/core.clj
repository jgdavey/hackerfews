(ns hackerfews.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]
            [clojure.pprint :as pprint]))

(def url (java.net.URI. "https://news.ycombinator.com/"))

(defn titles
  [docx]
  (let [tags (html/select docx [:td.title :a])]
    (map #(vector (apply str (:content %)) (:href (:attrs %))) tags)))

(defn numbers-in-nodes
  [docx selector]
  (let [x (flatten (map :content (html/select docx selector)))]
    (map #(Integer. (or (re-find #"\d+" %) "0")) x)))

(defn comment-counts
  [docx]
  (numbers-in-nodes docx [:td.subtext html/last-child]))

(defn points
  [docx]
  (numbers-in-nodes docx [:td.subtext html/first-child]))

(defn articles
  [resource]
  (map (fn [[t a] c p] { :title t :url a :comments c :points p})
       (titles resource)
       (comment-counts resource)
       (points resource)))

(defn get-url
  [url]
  (articles (html/html-resource url)))

(defn quality-articles [url]
  (filter (fn [article]
            (and (< (:comments article) (:points article))
                 (:title article)))
          (get-url url)))

(defn format-article [article]
  (format "%s\n%s\n" (:title article) (:url article)))

(defn -main []
  (doseq [article (quality-articles url)]
    (println (format-article article)))
  "")
