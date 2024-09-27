package com.schoolkiller.domain

enum class PromptText(val promptText: String) {
    SOLVE_PROMPT(
        "Solve this task (as grade+th grader). " +
                "Show the solution and explain (briefly) how to get there, " +
                "Explain using (language shown on this picture). " +
                "Use a chain of thoughts before answering."
    ),
    CHECK_SOLUTION_PROMPT(
        "Check this solution and rate it on scale (1–100), " +
                "Explain (as grade+th grader)." +
                "Explain using (language shown on this picture). " +
                "Use a chain of thoughts before answering."
    )
}