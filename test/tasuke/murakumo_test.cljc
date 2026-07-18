(ns tasuke.murakumo-test
  (:require [clojure.test :refer [deftest is]]
            [tasuke.murakumo :as tasuke]))

(def full-attestations
  (into {}
        (map (fn [gate] [gate (str "attested-" (name gate))]))
        (distinct (mapcat :required-gates (vals tasuke/cell-specs)))))

(deftest maps-all-legacy-socialsecurity-cells
  (is (= #{"socialsecurity_eligibility"
           "socialsecurity_mcp_facade"
           "socialsecurity_notice"
           "socialsecurity_outreach"
           "socialsecurity_publish"
           "socialsecurity_vow_intake"}
         (set (map :legacy-cell (vals tasuke/cell-specs))))))

(deftest r0-gates-block-effects
  (let [plan (tasuke/cell-plan :vow-intake
                               {:member-did "did:example:member"
                                :vow-cid "bafkreivow"})]
    (is (= :blocked (:status plan)))
    (is (= [:council-section-1-16-ratification
            :tasuke-baseline-review
            :charter-rider-scan-baseline
            :member-consent-baseline
            :pii-encrypted-envelope-baseline
            :cash-stipend-zero-baseline
            :member-signed-baseline
            :no-platform-held-key-baseline
            :non-coercive-right-of-exit-baseline
            :murakumo-only-inference-baseline
            :kotoba-only-substrate-baseline
            :transparent-force-audit-baseline
            :live-delivery-enabled-baseline
            :signed-vow-payload-baseline
            :adherent-sbt-mint-path-baseline
            :ipfs-pin-cid-baseline
            :eavt-datom-baseline
            :sybil-framework-baseline
            :no-coercion-baseline]
           (:missing-gates plan)))
    (is (empty? (:effects plan)))))

(deftest vow-intake-never-server-signs
  (let [plan (tasuke/cell-plan :vow-intake
                               {:attestations full-attestations
                                :member-did "did:example:member"
                                :vow-cid "bafkreivow"
                                :vow-signature-cid "bafkreisig"
                                :sbt-id "sbt-001"})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= "com.etzhayyim.socialsecurity.commitmentVow" (:collection effect)))
    (is (= 0 (get-in effect [:record :cashStipendUsd])))
    (is (= false (get-in effect [:record :serverSigned])))
    (is (= false (get-in effect [:record :platformHeldKey])))))

(deftest eligibility-is-in-kind-and-private
  (let [attestations (dissoc full-attestations :level-zero-in-kind-only-baseline)
        plan (tasuke/cell-plan :eligibility
                               {:attestations attestations
                                :entitlement-id "ent-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:level-zero-in-kind-only-baseline] (:missing-gates plan)))))

(deftest notice-requires-opt-in-and-unsubscribe
  (let [attestations (-> full-attestations
                         (dissoc :opt-in-baseline)
                         (dissoc :unsubscribe-honored-baseline))
        plan (tasuke/cell-plan :notice
                               {:attestations attestations
                                :notice-id "notice-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:opt-in-baseline :unsubscribe-honored-baseline]
           (:missing-gates plan)))))

(deftest outreach-is-not-targeted-advertising
  (let [attestations (dissoc full-attestations :no-microtargeting-baseline)
        plan (tasuke/cell-plan :outreach
                               {:attestations attestations
                                :post-id "post-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:no-microtargeting-baseline] (:missing-gates plan)))))

(deftest publish-is-aggregate-and-pii-free
  (let [plan (tasuke/cell-plan :publish
                               {:attestations full-attestations
                                :metric-id "metric-001"
                                :aggregate-period "2026-Q2"})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= "com.etzhayyim.socialsecurity.metricReport" (:collection effect)))
    (is (= true (get-in effect [:record :aggregateOnly])))
    (is (= true (get-in effect [:record :publicArtifactPiiFree])))))

(deftest mcp-facade-returns-unsigned-payload-only
  (let [plan (tasuke/cell-plan :mcp-facade
                               {:attestations full-attestations
                                :tool-id "socialSecurity.beginVow"
                                :payload-id "payload-001"})
        effects (:effects plan)]
    (is (= :ready (:status plan)))
    (is (= ["com.etzhayyim.socialsecurity.mcpToolRegistration"
            "com.etzhayyim.socialsecurity.unsignedVowPayload"]
           (mapv :collection effects)))
    (is (every? #(= false (get-in % [:record :serverSigned])) effects))
    (is (every? #(= true (get-in % [:record :unsignedPayloadOnly])) effects))))

(deftest all-cell-plans-ready-when-attested
  (let [plans (tasuke/all-cell-plans {:attestations full-attestations
                                      :member-did "did:example:member"
                                      :vow-cid "bafkreivow"
                                      :vow-signature-cid "bafkreisig"
                                      :sbt-id "sbt-001"
                                      :entitlement-id "ent-001"
                                      :notice-id "notice-001"
                                      :post-id "post-001"
                                      :metric-id "metric-001"
                                      :tool-id "socialSecurity.beginVow"
                                      :payload-id "payload-001"
                                      :consent-cid "bafkreiconsent"
                                      :aggregate-period "2026-Q2"
                                      :computed-at "2026-06-29T00:00:00Z"})]
    (is (= (set (keys tasuke/cell-specs)) (set (keys plans))))
    (is (every? #(= :ready (:status %)) (vals plans)))
    (is (= 7 (count (mapcat :effects (vals plans)))))))
