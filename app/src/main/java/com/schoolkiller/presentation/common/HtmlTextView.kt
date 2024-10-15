package com.schoolkiller.presentation.common

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

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
                       // println("Loaded page content is: ${it.toJson()}")
                        onValueChange(it.toJson())
                    }
                }

                // Load the HTML content with the scripts and contenteditable div
                loadDataWithBaseURL(
                    null,
                    createHtmlContent(content, isHtmlEditable.value),
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
                onValueChange(it.toJson())
            }
        }
    )
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