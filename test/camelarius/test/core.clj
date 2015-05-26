(ns camelarius.test.core
  (:use [clojure.test]
        [midje.sweet]
        [camelarius.core]))

(facts "Camel sanity checks"

  (let [context (make-context)
        produce (make-producer context)
        counter (atom 0)]
    (defroute context
      :err-handler (default-error-handler)
      (from "direct:test")
      (process (processor
                (swap! counter inc))))
    (produce "direct:test" "foo")
    (fact "produce and route"
      @counter => 1)))
