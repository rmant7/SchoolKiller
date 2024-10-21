package com.schoolkiller.domain.prompt

enum class Prompt(val text: String) {

    OCR_PROMPT(
        "Recognize text from this image. " +
                "If image doesn't have text, describe what you see in it. " +

                "If image contains geometric figures describe in detail  " +
                "the geometric figures, their relationships, " +
                "and which variables are known if you recognize any, " +
                "but answer only in language identified on the image."
    ),
    NO_HTML_OCR_SYSTEM_INSTRUCTION(
        "Answer only in language identified on the image." +
                "Separate each task if you see multiple ones." +
                "Use mathematical symbols instead of markdown." +

                "If you recognize any geometric figures describe in language " +
                "identified on the image recognized shapes, " +
                "known and unknown variables if you see any. " +

                "If you recognize math formulas format them using math symbols, example: " +
                "sin (ω θ) → T = 2π/ω. " +

                // This line removed ** symbols
                "Don't include font style formatting." +
                "Don't include images in your response. " +
                "Don't replace images with whitespaces."
    ),
    HTML_OCR_SYSTEM_INSTRUCTION(
        "Answer only in language identified on the image." +
                "Separate each task if you see multiple ones." +

                "If you recognize any geometric figures describe in language " +
                "identified on the image recognized shapes, " +
                "known and unknown variables if you see any. " +

                "Use Html tags instead of markdown. " +
                "Don't include images in your response. "
    ),
    NO_HTML_REQUEST(
        "Use mathematical symbols instead of markdown. "
                +
                "If you recognize math formulas format them using math symbols, example: " +
                "sin (ω θ) → T = 2π/ω. "
                +
                // This line removed ** symbols
                "Don't include font style formatting."
    ),
    HTML_REQUEST(
        "Use Html tags instead of markdown. " +
                "Don't include images in your response. "
    )

}