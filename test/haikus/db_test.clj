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


(comment (deftest find-term-test
   (add-term test-db-spec "robot" 2)
   (testing "Lookup for a term in the DB"
     (is (= 2 )))
   (testing "Lookup for a term not in the DB should add to term miss"
     (is (= false ))
     (is (= 1 )))
   (testing "Lookup for each term not in the DB should increment misses"
     (is (= 1 (find-term-miss test-db-spec "robotics")))
     (is (= false (find-term test-db-spec "robotics")))
     (is (= false (find-term test-db-spec "robotics")))
     (is (= 3 (find-term-miss test-db-spec "robotics"))))
   (testing "Adding a miss to the terms should remove it from misses"
     (is (= false (find-term test-db-spec "robotics")))
     (add-term test-db-spec "robotics" 3)
     (is (= 3 (find-term test-db-spec "robotics")))
     (is (= false (find-term-miss test-db-spec "robotics"))))))
