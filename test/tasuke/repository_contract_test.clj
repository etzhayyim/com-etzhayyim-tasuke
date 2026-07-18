(ns tasuke.repository-contract-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(def canonical-edn
  ["manifest.edn" "identity.edn" "dependencies.edn" "repository-contracts.edn"
   "data/seed-cybercrime-cases.kotoba.edn"
   "schema/cybercrime-victim-support-ontology.kotoba.edn"])

(deftest canonical-files-are-readable-edn
  (doseq [path canonical-edn]
    (testing path (is (some? (edn/read-string (slurp path)))))))

(deftest legacy-layout-is-absent
  (doseq [path ["actor.edn" "manifest.jsonld" "methods/publish.bb" "run_tests.sh"]]
    (is (not (.exists (io/file path))) path)))

(deftest external-json-is-wire-only
  (let [root (.getCanonicalFile (io/file "."))]
    (doseq [f (filter #(and (.isFile %)
                            (re-find #"\\.(json|jsonld)$" (.getName %)))
                      (file-seq root))]
      (let [rel (str (.relativize (.toPath root) (.toPath f)))]
        (is (or (.startsWith rel "wire/") (= rel ".well-known/did.json")) rel)))))

(deftest deprecated-language-artifacts-are-absent
  (doseq [f (filter #(.isFile %) (file-seq (io/file ".")))]
    (is (not (re-find #"\\.(go|tinygo|sh)$" (.getName f))) (str f))))
