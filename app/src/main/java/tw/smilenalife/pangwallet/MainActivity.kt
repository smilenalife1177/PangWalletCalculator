package tw.smilenalife.pangwallet

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    companion object {
        private const val CALCULATOR_URL =
            "https://smilenalife1177.github.io/lina-calculator/?utm_source=app&utm_medium=android&utm_content=pangwallet_app"
        private const val CALCULATOR_HOST = "smilenalife1177.github.io"
        private const val CALCULATOR_PATH = "/lina-calculator"
    }

    private lateinit var webView: WebView
    private lateinit var statusText: TextView

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(255, 250, 246))
        }

        root.addView(buildTopBar(), LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(64)
        ))

        statusText = TextView(this).apply {
            text = "正在載入 26 合 1 生活計算…"
            textSize = 12f
            setTextColor(Color.rgb(139, 129, 123))
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setBackgroundColor(Color.rgb(255, 247, 242))
        }
        root.addView(statusText, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(28)
        ))

        webView = WebView(this).apply {
            setBackgroundColor(Color.rgb(255, 250, 246))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.setSupportZoom(false)
            settings.displayZoomControls = false
            settings.builtInZoomControls = false
            settings.userAgentString = settings.userAgentString + " PangWalletCalculatorApp/1.0"
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    statusText.text = "26 合 1 已就位 ・ 收藏與最近使用會保留"
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url ?: return false
                    val isCalculator = uri.host == CALCULATOR_HOST &&
                        (uri.path?.startsWith(CALCULATOR_PATH) == true)

                    return if (isCalculator) {
                        false
                    } else {
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (_: Exception) {
                            Toast.makeText(this@MainActivity, "目前無法開啟這個連結", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        statusText.text = "網路暫時沒接上 ・ 桌面 Widget 仍可離線計算"
                        showOfflinePage()
                    }
                }
            }
        }

        root.addView(webView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        setContentView(root)
        webView.loadUrl(CALCULATOR_URL)
    }

    private fun buildTopBar(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(10), dp(8))
            setBackgroundColor(Color.rgb(247, 216, 203))
        }

        val icon = ImageView(this).apply {
            setImageResource(R.drawable.mingpang)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            contentDescription = "名胖"
        }
        bar.addView(icon, LinearLayout.LayoutParams(dp(50), dp(50)))

        val titles = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(7), 0, dp(6), 0)
        }
        titles.addView(TextView(this).apply {
            text = "胖錢包計算機"
            textSize = 18f
            setTextColor(Color.rgb(61, 58, 56))
        })
        titles.addView(TextView(this).apply {
            text = "26 合 1 生活計算 × 桌面 Widget"
            textSize = 11f
            setTextColor(Color.rgb(123, 85, 74))
        })
        bar.addView(titles, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        bar.addView(Button(this).apply {
            text = "＋桌面"
            textSize = 13f
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(Color.rgb(226, 111, 120))
            setOnClickListener { pinWidget() }
        }, LinearLayout.LayoutParams(dp(84), dp(46)))

        return bar
    }

    private fun showOfflinePage() {
        val html = """
            <!doctype html><html lang="zh-Hant"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <style>
            body{margin:0;background:#fffaf6;color:#3d3a38;font-family:sans-serif;display:flex;min-height:80vh;align-items:center;justify-content:center;text-align:center}
            .c{padding:30px;max-width:340px}.face{font-size:72px}h2{margin:14px 0 8px}p{color:#8b817b;line-height:1.7}
            button{border:0;border-radius:16px;background:#3d3a38;color:#fff;padding:14px 22px;font-size:16px}
            </style></head><body><div class="c"><div class="face">👛</div>
            <h2>網路先去喝水了</h2><p>26 合 1 需要連到最新生活計算頁。<br>桌面上的胖錢包 Widget 仍然可以離線做四則運算。</p>
            <button onclick="location.href='$CALCULATOR_URL'">重新連線</button></div></body></html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://smilenalife1177.github.io/", html, "text/html", "UTF-8", null)
    }

    private fun pinWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, WalletCalculatorWidget::class.java)

        if (manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(provider, null, null)
        } else {
            Toast.makeText(
                this,
                "請長按手機桌面 → 小工具 → 胖錢包計算機",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
