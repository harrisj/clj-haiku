(ns haikus.db-test
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [haikus.db :refer :all]))


(def test-db-spec {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname "haiku_test.db"})

(defn setup []
  (delete-db test-db-spec)
  (create-db test-db-spec))

(defn teardown []
  (delete-db test-db-spec))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :each each-fixture)

(deftest cmu-syllable-count-test
  (testing "A single syllable"
    (is (= 1 (cmu-syllable-count "AH0"))))
  (testing "3 syllable string"
    (is (= 3 (cmu-syllable-count "AH0 B R EY1 ZH AH0 N Z")))))

(deftest find-term-test
  (add-term test-db-spec "robot" 2)
  (testing "Lookup for a term in the DB"
    (is (= 2 (find-term test-db-spec "robot"))))
  (testing "Lookup for a term not in the DB should add to term miss"
    (is (= false (find-term test-db-spec "robotics")))
    (is (= 1 (find-term-miss test-db-spec "robotics"))))
  (testing "Lookup for each term not in the DB should increment misses"
    (is (= 1 (find-term-miss test-db-spec "robotics")))
    (is (= false (find-term test-db-spec "robotics")))
    (is (= false (find-term test-db-spec "robotics")))
    (is (= 3 (find-term-miss test-db-spec "robotics"))))
  (testing "Adding a miss to the terms should remove it from misses"
    (is (= false (find-term test-db-spec "robotics")))
    (add-term test-db-spec "robotics" 3)
    (is (= 3 (find-term test-db-spec "robotics")))
    (is (= false (find-term-miss test-db-spec "robotics")))))
