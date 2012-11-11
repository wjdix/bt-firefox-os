(defproject bt-firefox-os "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :repositories {"local" ~(str (.toURI (java.io.File. "repo")))}
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.2.2"]
                 [enlive "1.0.0"]
                 [cheshire "4.0.0"]
                 [braintree/braintree "2.19.0"]])
