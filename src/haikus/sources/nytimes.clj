(ns haikus.sources.nytimes
  (:require
    [clojure.string :as str]
    [net.cgrand.enlive-html :as html]))

;; I just stole a lot of this from David Nolen's Enlive demo
(def ^:dynamic *base-url* "http://nytimes.com")

(def ^:dynamic *story-selector*
     [[:article.story
       (html/but :.advertisement)
       (html/but :.autosStory)
       (html/but :.adCreative)]])

(def ^:dynamic *byline-selector* [html/root :> :.byline])
(def ^:dynamic *summary-selector* [html/root :> :.summary])

(def ^:dynamic *headline-selector*
     #{[html/root :> :h2 :a],
       [html/root :> :h3 :a]
       [html/root :> :h5 :a]})

(def ^:dynamic *url-selector*
     #{[html/root :> :h2 :a ]})

(defn split-on-space [word]
  "Splits a string on words"
  (str/split word #"\s+"))

(defn squish [line]
  (if (nil? line) 
    nil
    (str/triml (str/join " " (split-on-space (str/replace line #"\n" " "))))))

(defn extract [node]
  (let [headline (first (html/select [node] *headline-selector*))
        summary  (first (html/select [node] *summary-selector*))
        url      (get-in headline [:attrs :href])
        result   (conj (map html/text [headline summary]) url)]
    (zipmap [:url :headline :summary] (map squish result))))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn story-divs []
  (html/select (fetch-url *base-url*) *story-selector*))

(defn empty-story? [node]
  (every? (fn [[k v]] (= v "")) node))

(defn story-list []
  (remove empty-story? (map extract (story-divs))))
