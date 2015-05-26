(def camel-version "2.11.0")

(defproject clj-camel-holygrail "0.3.2" 
  :description "Apache Camel DSL in Clojure"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.apache.camel/camel-core ~camel-version]
                 [org.apache.camel/camel-jetty ~camel-version]
                 [org.apache.camel/camel-jms ~camel-version]
                 [org.apache.activemq/activemq-camel "5.8.0"]
                 [org.jboss.netty/netty "3.2.7.Final"]
                 [org.clojure/tools.logging "0.2.6"]]
  :source-paths ["src"]

  :lein-release {:deploy-via :shell
                 :shell ["./bin/release.sh"]}
  :profiles {:dev {:dependencies [[midje "1.5.1"]
                                  [midje-junit-formatter "0.1.0-SNAPSHOT"]]
                   :plugins [[lein-midje "3.0.0"]]
  }})
