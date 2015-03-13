(ns haikus.text
   (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [opennlp.nlp :as nlp]
    [haikus.db :as db])
   (:use [slingshot.slingshot :only [throw+ try+]]))

(def split-into-sentences (nlp/make-sentence-detector "resources/en-sent.bin"))
(def tokenize-into-words (nlp/make-tokenizer "resources/en-token.bin"))


(def numbers-to-names
    {"0" "ZERO", "1" "ONE", "2" "TWO", "3" "THREE", "4" "FOUR", "5" "FIVE", "6" "SIX", "7" "SEVEN",
    "8" "EIGHT", "9" "NINE", "10" "TEN", "11" "ELEVEN", "12" "TWELVE", "13" "THIRTEEN", "14" "FOURTEEN",
    "15" "FIFTEEN", "16" "SIXTEEN", "17" "SEVENTEEN", "18" "EIGHTEEN", "19" "NINETEEN", "20" "TWENTY",
    "30" "THIRTY", "40" "FORTY", "50" "FIFTY", "60" "SIXTY", "70" "SEVENTY", "80" "EIGHTY", "90" "NINETY"})

; Taken from old clojure contrib
(defmacro cond-let
  "Takes a binding-form and a set of test/expr pairs. Evaluates each test
  one at a time. If a test returns logical true, cond-let evaluates and
  returns expr with binding-form bound to the value of test and doesn't
  evaluate any of the other tests or exprs. To provide a default value
  either provide a literal that evaluates to logical true and is
  binding-compatible with binding-form, or use :else as the test and don't
  refer to any parts of binding-form in the expr. (cond-let binding-form)
  returns nil."
  [bindings & clauses]
  (let [binding (first bindings)]
    (when-let [[test expr & more] clauses]
      (if (= test :else)
        expr
        `(if-let [~binding ~test]
           ~expr
           (cond-let ~bindings ~@more))))))

(defn normalized-term
  "Normalizes the term for checking in the DB"
  [term]
  (str/upper-case (str/replace term #"[\.!?;,]$" "")))

(defn- find-term-or-throw
  "Looks up the term in the DB. Otherwise, it throws an exception"
  [term]
  (let [normalized (normalized-term term)] 
    (if-let [count (db/find-term normalized)]
       count
       (throw+ {:type :miss :term term :normalized normalized}))))

(comment defn syllable-count
  "Returns the syllable count for a term or raises an exception"
  [term]

  (cond-let [r (re-find #"^(\w+)\-(\w+)$" term)]
              (+ (syllable-count (nth r 1))
                 (syllable-coint (nth r 2)))
            [r (re-find #"^(anti|un|non|pre|post|re|micro|super|hyper|mega|over|micro|cyber|ultra)(.+)" term)]
              (+ (syllable-count (nth r 1))
                 (syllable-count (nth r 2)))
            :else
              (find-term-or-throw term)))


