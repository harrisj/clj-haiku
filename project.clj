(defproject haikus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [enlive "1.1.5"]
                 [yesql "0.4.0"]
                 [slingshot "0.12.2"]
                 [clojure-opennlp "0.3.3"]
                 [expectations "2.0.9"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :main ^:skip-aot haikus.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
