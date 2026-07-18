(babashka.classpath/add-classpath "src:test")
(require '[clojure.test :as t])

(def suites
  '[tasuke.cells.test-state-machines
    tasuke.methods.test-analyze
    tasuke.methods.test-app-parity
    tasuke.methods.test-charter-invariants
    tasuke.methods.test-consistency
    tasuke.methods.test-evidence
    tasuke.methods.test-intake
    tasuke.methods.test-lexicons
    tasuke.methods.test-packet
    tasuke.methods.test-report-gen
    tasuke.methods.test-triage
    tasuke.repository-contract-test])
(apply require suites)
(let [{:keys [fail error]} (apply t/run-tests suites)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
