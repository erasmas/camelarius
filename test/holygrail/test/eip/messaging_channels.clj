(ns camelarius.test.eip.messaging-channels
  (:use [camelarius.core]
        [camelarius.test-util]
        [clojure.test]
        [midje.sweet]))

(facts "Point to Point Channel EIP"
  (fact "point to point channel"
    (let [context (make-context)]

      (defroute context
        (from "seda:source")
        (to "mock:dest"))

      ((make-producer context) "seda:source" "body")

      ; this is hacky
      (Thread/sleep 25)

      (received-counter (make-endpoint context "mock:dest"))
      => 1)))

(facts "Publish Subscribe Channel EIP"
  (fact "publish/subscribe"
    (let [context (make-context)]

      (defroute context
        (from "seda:source?multipleConsumers=true")
        (to "mock:dest"))

      (defroute context
        (from "seda:source?multipleConsumers=true")
        (to "mock:dest"))

      ((make-producer context) "seda:source" "body")

      ; this is hacky
      (Thread/sleep 25)

      (received-counter (make-endpoint context "mock:dest"))
      => 2)))

(facts "Dead Letter Channel EIP"
  (fact "DLQ error handler"
    (let [context (make-context)]

      (defroute context
        :err-handler (dead-letter-channel "mock:dlq"
                      (maximum-redeliveries 0))
        (from "direct:source")
        (process (processor (throw (Exception. "foo")))))

      ((make-producer context) "direct:source" "body")
      (received-counter (make-endpoint context "mock:dlq"))
      => 1)))

(facts "Guaranteed delivery EIP"
  (fact "guaranteed delivery using file"
    (let [context (make-context)
          latch (countdown-latch 1)]

      (defroute context
        (from "direct:source")
        (to "file:/tmp/clj-camel-camelarius-tests"))

      (defroute context
        (from "file:/tmp/clj-camel-camelarius-tests?move=.done")
        (pipeline)
        (to "mock:dest")
        (process (processor (countdown latch))))

      ((make-producer context) "direct:source" "file-body")

      (wait latch 5000)

      (received-counter (make-endpoint context "mock:dest"))
      => 1)))

(facts "Message Bus EIP"
  (fact "message bus - decouple pub sub"
    (let [pub-context (make-context)
          sub-context (make-context)
          latch (countdown-latch 1)]

      (defroute pub-context
        (from "direct:source")
        (to "vm:pub-output"))

      (defroute sub-context
        (from "vm:pub-output")
        (pipeline)
        (to "mock:dest")
        (process (processor (countdown latch))))

      ((make-producer pub-context) "direct:source" "body")

      (wait latch 5000)
      (received-counter (make-endpoint sub-context "mock:dest"))
      => 1)))
