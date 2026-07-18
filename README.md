# tasuke - Social Security Assistance Actor

**DID**: `did:web:tasuke.etzhayyim.com`
**Namespace**: `com.etzhayyim.socialsecurity.*`
**Status**: migration boundary for socialsecurity domain cells

## Migration Boundary

`src/tasuke/murakumo.cljc` is the Murakumo-facing cljc actor boundary for the
legacy socialsecurity kotoba-kotodama cells:

- `socialsecurity_outreach` -> `outreachPost`
- `socialsecurity_vow_intake` -> `commitmentVow`
- `socialsecurity_eligibility` -> `entitlement`
- `socialsecurity_notice` -> `noticeEmail`
- `socialsecurity_publish` -> `metricReport`
- `socialsecurity_mcp_facade` -> `mcpToolRegistration`, `unsignedVowPayload`

The actor is assistance and enrollment infrastructure only. It never produces
cash benefits, never signs for a member, never publishes member PII, and never
uses targeted advertising or profiling. Live delivery and publication remain
blocked until Council ratification, consent, encryption, aggregate-only, and
Transparent Force audit attestations are present.
