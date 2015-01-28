(ns haikus.db
   (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [yesql.core :refer [defquery defqueries]]))


(def default-db-spec {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname "haiku.db"})

(defn db-name [db-spec] (get db-spec :subname))

(defn- exec-create-query
  [db-spec sym]
  (do
    (eval (sym db-spec))
  )
)

(defn delete-db
  [db-spec]
  (io/delete-file (db-name db-spec) true)
)

(defn create-db
  "Creates the database from scratch"
  [db-spec]
  (let [queries (defqueries "sql/create.sql")]
    (println "Creating" (db-name db-spec))
    (doall (map (partial exec-create-query db-spec) queries))))

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
  ([db-spec term syllables from-cmu]
   (db-add-term! db-spec (str/upper-case term) syllables from-cmu)
   (db-set-varies-for-term! db-spec (str/upper-case term) syllables)
   (db-remove-term-from-misses! db-spec (str/upper-case term)))
  ([db-spec term syllables]
   (add-term db-spec term syllables false)))

(defn find-term
  "Looks up a term in the DB and returns a syllable count if found or false if not"
  [db-spec term]
  (let [result (db-lookup-term db-spec (str/upper-case term))]
    (if (seq result)
      (get (first result) :syllables)
      (do
        (db-add-term-to-misses! db-spec (str/upper-case term))
        false
      )
    )
  )
)

(defn find-term-miss
  "Looks up a term in the DB and returns a miss count if found or false if not"
  [db-spec term]
  (let [result (db-lookup-term-miss db-spec (str/upper-case term))]
    (if (seq result)
      (get (first result) :miss_count)
      false
    )
  )
)


(defn load-cmu-dict-into-db
  [db-spec]
  (println "Loading initial dictionary. This might take a while...")
  (doall (map (fn [[term phonetic]]
       (if (re-find #"\(\d+\)$" term)
         (let [canonical-term (str/replace term #"\(\d+\)$" "")]
           (add-term canonical-term (cmu-syllable-count phonetic))
         )
         (add-term db-spec term (cmu-syllable-count phonetic) true)
        )) raw-cmu-terms))
)

(defn initialize-db
  [db-spec]
  (create-db db-spec)
  (load-cmu-dict-into-db db-spec)
)

;; not needed for now
;;(comment (defn cmu-terms-to-map
;;  [cmu-terms]
;;  (let [out {}]
;;    (into out
;;       (map (fn [[term phonetic]]
;;                (if (re-find #"\(\d+\)$" term)
;;                  ;; check if syllable count different from prior entry for alternate(1) cmu temrs
;;                  (let [canonical-term (str/replace term #"\(\d+\)$" "")
;;                        scount (cmu-syllable-count phonetic)]
;;                    (if-let [prior (get out canonical-term)]
;;                      (let [prior-scount (:count prior)
;;                            ;; do we already have varies set to true
;;                            varies (or (:varies prior) (not= (prior-scount scount)))]
;;                        (vector canonical-term {:term canonical-term :count scount :varies varies}))
;;                      (vector canonical-term {:term canonical-term :count scount :varies false})))
;;                  (vector term {:term term :count (cmu-syllable-count phonetic) :varies false})))
;;            cmu-terms)))))
;;
