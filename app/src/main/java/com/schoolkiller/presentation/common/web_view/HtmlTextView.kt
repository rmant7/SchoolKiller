package com.schoolkiller.presentation.common.web_view

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.text.Bidi

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlTextView(
    htmlContent: String,
    isEditable: MutableState<Boolean>,
    onValueChange: (String) -> Unit = {}
) {
    val content by remember { mutableStateOf(htmlContent) }
    val isHtmlEditable by remember { mutableStateOf(isEditable) }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = AppWebView().getInstance {
                    // get content every time page is loaded
                    // so that viewmodel gets not null, but valid content value
                    evaluateJavascript("getContent()") {
                        val cleanedStr = cleanHtmlStr(it)
                        //println(cleanedStr)
                        onValueChange(cleanedStr)
                    }
                }

                // Load the HTML content with the scripts and contenteditable div
                loadDataWithBaseURL(
                    null,
                    createKatexHtml(content, isHtmlEditable.value, isLtr(content)),
                    //createHtmlContent(content, isHtmlEditable.value),
                    "text/html",
                    "utf-8",
                    null
                )
            }
        },
        update = { webView ->
            // could be one function to avoid 2 update view calls

            // set text to be editable or not
            webView.evaluateJavascript(
                "setEditable(${isHtmlEditable.value})", null
            )

            // get content every time user changes prompt
            webView.evaluateJavascript("getContent()") {
                //println("User changed content is: ${it.toJson()}")
                onValueChange(cleanHtmlStr(it))
            }
        }
    )
}

private fun isLtr(content: String): Boolean {
    val bidi = Bidi(
        content,
        Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT
    )
   return bidi.isLeftToRight
}

private fun cleanHtmlStr(str: String): String {
    return str.substring(1, str.length - 1)
        .replace("\\n", "") // Remove all newline characters
        .replace("\\u003C", "<") // Replace \u003C with <
        .replace("\\u003E", ">") // Replace \u003E with >
        .replace("\\\"", "\"") // Replace escaped quotes with actual quotes
        .trim()
}

private fun createKatexHtml(
    content: String,
    isEditable: Boolean,
    isLTR: Boolean
): String {
    val dir = if(isLTR) "ltr" else "rtl"
    return """
    <!DOCTYPE html>
    <head>
       <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.11/dist/katex.min.css" integrity="sha384-nB0miv6/jRmo5UMMR1wu3Gz6NLsoTkbqJghGIsx//Rlm+ZU03BU6SQNC66uf4l5+" crossorigin="anonymous">
       <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.11/dist/katex.min.js" integrity="sha384-7zkQWkzuo3B5mTepMUcHkMB5jZaolc2xDwL6VFqjFALcbeS9Ggm/Yr2r3Dy4lfFg" crossorigin="anonymous"></script>
       <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.11/dist/contrib/auto-render.min.js" integrity="sha384-43gviWU0YVjaDtb/GhzOouOXtZMP/7XUzwPTstBeZFe/+rCMvRwr4yROQP43s0Xk" crossorigin="anonymous"></script>
       <script>
            function getContent() {
                return document.getElementById('editable').innerHTML;  
            }
                
            function setContent(content) {
                document.getElementById('editable').innerHTML = content;
            }
                
            function setEditable(edit) {
                document.getElementById('editable').setAttribute('contenteditable', edit);
            }
            
            function renderMath(){
                renderMathInElement(document.getElementById("editable").innerHTML);
            }
            
            document.addEventListener("DOMContentLoaded", function() {
                renderMathInElement(document.body, {
                    // customised options
                    // • auto-render specific keys, e.g.:
                    delimiters: [
                        {left: '$$', right: '$$', display: true},
                        {left: '$', right: '$', display: false},
                        {left: '\\(', right: '\\)', display: false},
                        {left: '\\[', right: '\\]', display: true}
                    ],
                    // • rendering keys, e.g.:
                    throwOnError : false
                });
            });
            
       </script>
    </head>
        <body>
            <div id="editable" contenteditable="$isEditable" dir="$dir">
               $content
            </div>
        </body>
    </html>
    """.trimIndent()

}

private fun createHtmlContent(content: String, isEditable: Boolean): String {
    val html = """
        <html>
        <head>
            <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
            <script type="text/javascript" async
                src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js">
            </script>
            <script>
                function getContent() {
                    return document.getElementById('editable').innerHTML;  
                }
                
                function setContent(content) {
                    document.getElementById('editable').innerHTML = content;
                }
                
                function setEditable(edit) {
                    document.getElementById('editable').setAttribute('contenteditable', edit);
                }
            </script>
        </head>
        <body>
            <div id="editable" contenteditable="$isEditable">
                $content
            </div>
        </body>
        </html>
    """.trimIndent()
    return html
}


private fun String.toJson(): String {
    return this.replace("\"", "\\\"")
}