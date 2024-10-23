package com.schoolkiller.presentation.common.web_view

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlTextView(
    htmlContent: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit = {},
    textAlign: LayoutDirection,
) {
    val content by remember { mutableStateOf(htmlContent) }
    val isNightMode = isSystemInDarkTheme()


    // Should be converted to css color scheme
    // instead of hard coded colours in css style
    /*
    val background = MaterialTheme.colorScheme.background
    val fontColor = MaterialTheme.colorScheme.primary
    */

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true

                webViewClient = AppWebView().getInstance {

                    // get content every time page is loaded
                    // so that viewmodel gets not null, but valid content value
                    evaluateJavascript("getContent()") {
                        onValueChange(
                            cleanHtmlStr(it)
                        )
                    }
                }

                // Load the HTML content with the scripts and contenteditable div
                loadDataWithBaseURL(
                    null,
                    createKatexHtml(
                        content,
                        isEditable,
                        getTextDirStr(textAlign),
                        isNightMode
                    ),
                    "text/html",
                    "utf-8",
                    null
                )
            }
        },
        update = { webView ->
            // could be one function to avoid 3 update view calls
            webView.evaluateJavascript(
                "setTextDir('${getTextDirStr(textAlign)}')", null
            )

            // set text to be editable or not
            webView.evaluateJavascript(
                "setEditable(${isEditable})", null
            )

            // get content every time user changes prompt
            webView.evaluateJavascript("getContent()") {
                onValueChange(
                    cleanHtmlStr(it)
                )
            }
        }

    )
}

private fun getTextDirStr(layoutDirection: LayoutDirection): String {
    return if (layoutDirection == LayoutDirection.Ltr) "ltr" else "rtl"
}

// Remove Kotlin String formatting
private fun cleanHtmlStr(str: String): String {
    val cleanedStr = str.substring(1, str.length - 1)
        .replace("\\n", "") // Remove all newline characters
        .replace("\\u003C", "<") // Replace \u003C with <
        .replace("\\u003E", ">") // Replace \u003E with >
        .replace("\\\"", "\"") // Replace escaped quotes with actual quotes
        .trim()
    return cleanedStr
}

// Katex Math rendering
private fun createKatexHtml(
    content: String,
    isEditable: Boolean,
    textDir: String,
    nightMode: Boolean
): String {
    // Should be converted to css color scheme
    // instead of hard coded colours in css style
    val background: String
    val fontColor: String
    if (nightMode) {
        background = "#14141C"
        fontColor = "white"
    } else {
        background = "white"
        fontColor = "black"
    }

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
            
            function setTextDir(dir){
                 document.getElementById('editable').setAttribute('dir', dir);
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
       
       <style>
       
       body {
        background-color: $background;
        color: $fontColor;
        font-size: 20px;
       }
       
       </style>
       
    </head>
        <body>
            <div id="editable" contenteditable="$isEditable" dir="$textDir">
               $content
            </div>
        </body>
    </html>
    """.trimIndent()

}
