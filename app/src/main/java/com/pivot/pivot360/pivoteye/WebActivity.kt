package com.pivot.pivot360.pivoteye

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_web.*


class WebActivity : AppCompatActivity(), GlassGestureDetector.OnGestureListener {

    private var glassGestureDetector: GlassGestureDetector? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_web)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        glassGestureDetector = GlassGestureDetector(this, this)

        val link = intent.getStringExtra("link")
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webView.loadUrl(link)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
        when (gesture) {
            GlassGestureDetector.Gesture.TAP ->
                // Response for TAP gesture
                return false
            GlassGestureDetector.Gesture.SWIPE_FORWARD ->
                // Response for SWIPE_FORWARD gesture
                return false
            GlassGestureDetector.Gesture.SWIPE_BACKWARD ->
                finish()
            else -> return false
        }
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (glassGestureDetector?.onTouchEvent(ev)!!) {
            true
        } else super.dispatchTouchEvent(ev)
    }
}
