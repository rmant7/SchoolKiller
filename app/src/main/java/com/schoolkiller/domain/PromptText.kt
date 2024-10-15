package com.schoolkiller.domain

enum class PromptText(val promptText: String) {
    SOLVE_PROMPT(
        "Solve this task (as grade+th grader). " +
                "Show the solution and explain (briefly) how to get there. " +
                "If there are multiple tasks, solve them all separately." +
                "Use a chain of thoughts before answering."
    ),
    CHECK_SOLUTION_PROMPT(
        "As a (grade+th grader) explain in detail" +
                " why this solution is correct or not, rate it on scale (1–100)." +
                "If there are multiple tasks, check them all separately."
    ),
    OCR_PROMPT(
        "Recognize text from this image. " +
                "If image doesn't have text, describe what you see in it. " +
                "If image contains geometric figures describe in detail  " +
                "the geometric figures, their relationships, " +
                "and which variables are known if you recognize any, " +
                "but answer only in language identified on the image."
    ),
    CHECK_SOLUTION_SYSTEM_INSTRUCTION(
        "Answer only in language identified on the image."
    ),
    OCR_SYSTEM_INSTRUCTION(
        "Answer only in language identified on the image." +
                "Separate each task if you see multiple ones."
    ),
    NO_HTML_REQUEST(
        "Use mathematical symbols instead of markdown." +
                "Follow this example if you recognize any geometric figures, " +
                "but answer only in language identified on the image: " +
                "The image shows several parallelograms. " +
                "1. Parallelogram ABCD has sides AB = 8, BC = 14, " +
                "AD = 8, CD = 14. Angle ABC is 120 degrees."
    ),
    HTML_REQUEST(
        "Use Html tags instead of markdown. " +
                "Don't include images in your response. "
        // + "Ignore text style. Ignore text font."
    )

}