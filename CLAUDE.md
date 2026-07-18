# com-etzhayyim-tasuke

`tasuke`（助）は無料の cybercrime-victim-support actor です。
旧 `etzhayyim/root/20-actors/tasuke` の実装と契約はこの flat west project が所有します。

## Contract

- metadata、identity、dependencies、ontology、lexicons、seed は EDN canonical。
- source は `src/tasuke/`、tests は `test/tasuke/`。
- JSON/JSON-LD は外部 wire のみ。Go/TinyGo と shell runner は deprecated。
- 支援は無料、本人作成・本人提出、警察 authored 不可、非裁定。
- evidence は PII-by-reference、no-server-key、live submission は gated。

Run `bb run_tests.clj` and `clojure -M:test`.
