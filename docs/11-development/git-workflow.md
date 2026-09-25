# Git Workflow

- Branch: `main` only — no feature branches (small academic project, sequential phases already provide structure).
- One commit per phase, using the message format in `implementation-plan.md` Section 4.
- Push after every phase; do not batch multiple phases into one push.
- Never force-push. Never rewrite history once pushed.
- If a regression is found in an already-pushed phase, fix forward with a new commit (`phase-NN: fix ...`), don't amend old commits.
