(defproject google-login "0.1.0-SNAPSHOT"
  :description "Script to login to site with google auth"
  :url ""
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]]
  :main google-login.core
  :profiles {:uberjar {:aot :all}})
