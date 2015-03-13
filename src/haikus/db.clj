(ns haikus.db
   (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [yesql.core :refer [defquery defqueries]]))


(def db-spec {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname "haiku.db"})

(defn db-name [] (get db-spec :subname))

(defn- exec-create-query
  [sym]
  (do
    (eval (sym db-spec))))

(defn delete-db
  []
  (io/delete-file (db-name) true))

(defn create-db
  "Creates the database from scratch"
  []
  (let [queries (defqueries "sql/create.sql")]
    (println "Creating" (db-name))
    (doall (map exec-create-query queries))))

(defn- single-cmu-term
  [s]
  (let [a (str/split s #"[ ]+")]
     [(first a) (str/join " " (rest a))]))

(def raw-cmu-terms
  (filter #(not (re-find #"^\W" (first %))) (map single-cmu-term (str/split-lines (slurp "data/cmudict.txt")))))

(defn cmu-syllable-count
  "Returns the number of syllables in a CMU phonetic string"
  [str]
  (let [a (str/split str #"[ ]+")]
     (count (filter #(re-find #"\d$" %) a))))

(defqueries "sql/terms.sql")

(defn add-term
  "Add or updates a term in the DB; if a prior version of the term is in the DB with a different count, set varies to true"
  ([term syllables from-cmu]
   (db-add-term! db-spec (str/upper-case term) syllables from-cmu)
   (db-set-varies-for-term! db-spec (str/upper-case term) syllables)
   (db-remove-term-from-misses! db-spec (str/upper-case term)))
  ([term syllables]
   (add-term term syllables false)))

(defn find-term
  "Looks up a term in the DB and returns a syllable count if found or false if not"
  [term]
  (let [result (db-lookup-term db-spec (str/upper-case term))]
    (if (seq result)
      (get (first result) :syllables)
      (do
        (db-add-term-to-misses! db-spec (str/upper-case term))
        false))))

(defn find-term-miss
  "Looks up a term in the DB and returns a miss count if found or false if not"
  [term]
  (let [result (db-lookup-term-miss db-spec (str/upper-case term))]
    (if (seq result)
      (get (first result) :miss_count)
      false)))


(defn load-cmu-dict-into-db
  []
  (println "Loading initial dictionary. This might take a while...")
  (doall (map (fn [[term phonetic]]
       (if (re-find #"\(\d+\)$" term)
         (let [canonical-term (str/replace term #"\(\d+\)$" "")]
           (add-term canonical-term (cmu-syllable-count phonetic))
         )
         (add-term term (cmu-syllable-count phonetic) true)))
          raw-cmu-terms)))

(defn initialize-db
  []
  (create-db)
  (load-cmu-dict-into-db))
