package com.schoolkiller.domain

enum class PromptText(val promptText: String) {
    SOLVE_PROMPT(
        "Solve this task (as grade+th grader). " +
                "Show the solution and explain (briefly) how to get there. " +
                "Explain only in (language identified on the sent file). " +
                "Use a chain of thoughts before answering."
    ),
    CHECK_SOLUTION_PROMPT(
        "Check the solution depicted on the sent file. " +
                "Rate it on scale (1–100)." +
                "Explain only in language identified on the sent file." + //(as grade+th grader)
                "Use a chain of thoughts before answering."
    )
}