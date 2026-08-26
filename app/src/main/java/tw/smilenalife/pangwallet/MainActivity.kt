package tw.smilenalife.pangwallet

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(40), dp(24), dp(32))
            setBackgroundColor(Color.rgb(255, 246, 241))
        }

        root.addView(TextView(this).apply {
            text = "👛 胖錢包計算機"
            textSize = 28f
            setTextColor(Color.rgb(67, 45, 37))
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        root.addView(TextView(this).apply {
            text = "這個 App 只負責把計算機放到桌面。\n放好後，平常不用再打開 App，直接在桌面按就能算。"
            textSize = 17f
            setTextColor(Color.rgb(95, 73, 65))
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, dp(28))
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        root.addView(Button(this).apply {
            text = "＋ 加到手機桌面"
            textSize = 18f
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(Color.rgb(226, 111, 120))
            setPadding(dp(18), dp(12), dp(18), dp(12))
            setOnClickListener { pinWidget() }
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(58)
        ))

        root.addView(TextView(this).apply {
            text = "也可以：長按手機桌面 → 小工具 / Widgets → 胖錢包計算機。\n\n放上桌面後可拖曳位置，也可以拉大、縮小。"
            textSize = 15f
            setTextColor(Color.rgb(120, 97, 88))
            gravity = Gravity.CENTER
            setPadding(0, dp(28), 0, 0)
        }, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(root)
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
}
