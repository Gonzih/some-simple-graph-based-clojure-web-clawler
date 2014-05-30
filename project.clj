(defproject crawler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.3.1"]
                 [enlive "1.1.5"]
                 [org.clojure/tools.logging "0.2.6"]]
  :plugins [[lein-simpleton "1.3.0"]]
  :main crawler.core)
