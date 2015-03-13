(ns haikus.db-test
  (:require [haikus.db :refer :all])
  (:use expectations))

(def test-db-spec {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname "haiku_test.db"})

(defn setup-db []
  (delete-db)
  (create-db)
  (add-term "robot" 2))

(defn teardown-db []
  (comment delete-db test-db-spec))

(defn in-context
  "setup and teardown the test DB first"
  {:expectations-options :in-context}
  [work]
  
  (with-redefs [db-spec test-db-spec]
    (setup-db)
    (work)
    (teardown-db)))

(expect 1 (cmu-syllable-count "AH0"))
(expect 3 (cmu-syllable-count "AH0 B R EY1 ZH AH0 N Z"))

(expect 2 (find-term "robot"))

;; On a miss, should return false and also not be in 
(expect (more-> false find-term
                 1     find-term-miss)
        "robotics")


;; Should increment misses
(expect (more-> 1 (find-term-miss "robotics")
                false (find-term "robotics")
                false (find-term "robotics")
                3  (find-term-miss "robotics")))

;; Adding a miss to term should remove it from misses
(expect (more-> false (find-term "robotics")
                nil (add-term "robotics" 3)
                3 (find-term "robotics")
                false (find-term-miss "robotics")))

