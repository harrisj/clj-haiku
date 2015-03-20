(ns haikus.text-test
  (:require [haikus.text :refer :all])
  (:use expectations))

;; Some normalized text tests
(expect "ROBOT" (normalized-term "robot"))
(expect "ROBOT" (normalized-term "robot."))
(expect "ROBOT" (normalized-term "robot,"))
(expect "ROBOT" (normalized-term "robot!"))
(expect "ROBOT" (normalized-term "robot:"))

;; test find-term-or-throw
(with-redefs [haikus.db/find-term #(if (% == "ROBOT") 2 false)]
  (expect 2 (find-term-or-throw "robot"))
  (expect Exception (find-term-or-throw "cassowary")))

(def test-lookup
  [term]

  (case term
    "robot" 2
    "self" 1
    "inflicted" 3
    :else false))

(with-redefs [find-term-or-throw test-lookup]
  (expect 2 (syllable-count "robot"))
  (expect 4 (syllable-count "self-inflicted"))
)
