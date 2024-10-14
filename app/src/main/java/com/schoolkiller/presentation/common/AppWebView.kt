package com.schoolkiller.presentation.common

import android.webkit.WebView
import android.webkit.WebViewClient
import javax.inject.Singleton

@Singleton
class AppWebView {

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
           onUpdate(view)
        }
    }

    private var onUpdate: (WebView?) -> Unit = {  }

    fun getInstance(onPageFinished: (WebView?) -> Unit): WebViewClient {
        onUpdate = onPageFinished
        return webViewClient
    }


}