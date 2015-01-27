(ns haikus.db-test
  (:require [clojure.test :refer :all]
            [haikus.db :refer :all]))

(binding [#'haikus.db/*db-name* "haiku_test.db"]
  (defn setup []
    (println "Setup")
    (create-db))

  (defn teardown []
    (println "Teardown")
    (comment delete-db))

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
   (add-term "robot" 2)
       (testing "Lookup for a term in the DB"
         (is (= 2 (find-term "robot"))))
       (testing "Lookup for a term not in the DB"
         (is (= false (find-term "robotics")))
         (is (= 1 (find-term-miss "robotics"))))
       (testing "Lookup for each term not in the DB should increment misses"
         (is (= false (find-term "robotics")))
         (is (= false (find-term "robotics")))
         (is (= 2 (find-term-miss "robotics"))))
       (testing "Adding a miss to the terms should remove it from misses"
         (is (= false (find-term "robotics")))
         (add-term "robotics" 3)
         (is (= 3 (find-term "robotics")))
         (is (= false (find-term-miss "robotics"))))
   )
)
