(ns haikus.text
   (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [opennlp.nlp :as nlp]
    [haikus.db :as db])
   (:use [slingshot.slingshot :only [throw+ try+]]))

;(def parser-file (io/file (io/resource "archieml.bnf" )))
(def split-into-sentences (nlp/make-sentence-detector (io/file (io/resource "en-sent.bin"))))
(def tokenize-into-words (nlp/make-tokenizer (io/file (io/resource "resources/en-token.bin"))))

(def numbers-to-names
    {"0" "ZERO", "1" "ONE", "2" "TWO", "3" "THREE", "4" "FOUR", "5" "FIVE", "6" "SIX", "7" "SEVEN",
    "8" "EIGHT", "9" "NINE", "10" "TEN", "11" "ELEVEN", "12" "TWELVE", "13" "THIRTEEN", "14" "FOURTEEN",
    "15" "FIFTEEN", "16" "SIXTEEN", "17" "SEVENTEEN", "18" "EIGHTEEN", "19" "NINETEEN", "20" "TWENTY",
    "30" "THIRTY", "40" "FORTY", "50" "FIFTY", "60" "SIXTY", "70" "SEVENTY", "80" "EIGHTY", "90" "NINETY"})

(defn normalized-term
  "Normalizes the term for checking in the DB"
  [term]
  (str/upper-case (str/replace term #"[^\w\d]$" "")))

(defn find-term
  "Looks up the term in the DB. Otherwise, it returns false"
  [term]

  (db/find-term (normalized-term term)))

(defn find-term-or-throw
  "Looks up the term in the DB. Otherwise, it throws an exception"
  [term]

  (if-let [count (find-term term)]
    count
    (throw+ {:type :miss :term term :normalized normalized})))

(defn hyphenated-term?
  "Returns a vector of the parts if it is, false otherwise"
  [term]

  (let [parts (str/split term #"\-")]
    (if (> (count parts) 1)
      (rest parts)
      false)))


(defn syllable-count
  "Returns the syllable count for a term or raises an exception"
  [term]

  (if-let [parts (hyphenated-term? term)]
    (reduce + 0 (map syllable-count parts))
    (if-let [])
    )
)
