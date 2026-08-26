package tw.smilenalife.pangwallet

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class WalletCalculatorWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_KEY = "tw.smilenalife.pangwallet.ACTION_KEY"
        private const val EXTRA_KEY = "calc_key"
        private const val PREFS = "pang_wallet_calc_state"
        private val MC = MathContext(16, RoundingMode.HALF_UP)

        private data class CalcState(
            var input: String = "0",
            var accumulator: String? = null,
            var operator: String? = null,
            var freshInput: Boolean = true
        )

        private fun prefKey(id: Int, field: String) = "w_${id}_$field"

        private fun loadState(context: Context, id: Int): CalcState {
            val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            return CalcState(
                input = p.getString(prefKey(id, "input"), "0") ?: "0",
                accumulator = p.getString(prefKey(id, "acc"), null),
                operator = p.getString(prefKey(id, "op"), null),
                freshInput = p.getBoolean(prefKey(id, "fresh"), true)
            )
        }

        private fun saveState(context: Context, id: Int, s: CalcState) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(prefKey(id, "input"), s.input)
                .putString(prefKey(id, "acc"), s.accumulator)
                .putString(prefKey(id, "op"), s.operator)
                .putBoolean(prefKey(id, "fresh"), s.freshInput)
                .apply()
        }

        private fun clearState(context: Context, id: Int) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .remove(prefKey(id, "input"))
                .remove(prefKey(id, "acc"))
                .remove(prefKey(id, "op"))
                .remove(prefKey(id, "fresh"))
                .apply()
        }

        private fun calculate(a: String, b: String, op: String): String {
            return try {
                val left = BigDecimal(a)
                val right = BigDecimal(b)
                val result = when (op) {
                    "+" -> left.add(right, MC)
                    "−" -> left.subtract(right, MC)
                    "×" -> left.multiply(right, MC)
                    "÷" -> {
                        if (right.compareTo(BigDecimal.ZERO) == 0) return "ERROR"
                        left.divide(right, MC)
                    }
                    else -> right
                }
                normalizeResult(result)
            } catch (_: Exception) {
                "ERROR"
            }
        }

        private fun normalizeResult(value: BigDecimal): String {
            val clean = value.stripTrailingZeros()
            val plain = clean.toPlainString()
            return if (plain.length <= 18) plain else clean.round(MathContext(12)).toEngineeringString()
        }

        private fun doPending(s: CalcState): Boolean {
            val a = s.accumulator ?: return false
            val op = s.operator ?: return false
            val result = calculate(a, s.input, op)
            s.input = result
            if (result == "ERROR") {
                s.accumulator = null
                s.operator = null
                s.freshInput = true
                return false
            }
            s.accumulator = result
            return true
        }

        private fun applyKey(s: CalcState, key: String) {
            when (key) {
                "C" -> {
                    s.input = "0"
                    s.accumulator = null
                    s.operator = null
                    s.freshInput = true
                }

                in "0".."9" -> {
                    if (s.input == "ERROR" || s.freshInput) {
                        s.input = key
                        s.freshInput = false
                    } else {
                        val digitCount = s.input.count { it.isDigit() }
                        if (digitCount < 14) {
                            s.input = if (s.input == "0") key else s.input + key
                        }
                    }
                }

                "." -> {
                    if (s.input == "ERROR" || s.freshInput) {
                        s.input = "0."
                        s.freshInput = false
                    } else if (!s.input.contains(".")) {
                        s.input += "."
                    }
                }

                "+", "−", "×", "÷" -> {
                    if (s.input == "ERROR") {
                        s.input = "0"
                        s.accumulator = null
                        s.operator = null
                    }

                    if (s.accumulator == null) {
                        s.accumulator = s.input
                    } else if (s.operator != null && !s.freshInput) {
                        doPending(s)
                    }

                    if (s.input != "ERROR") {
                        s.operator = key
                        s.freshInput = true
                    }
                }

                "=" -> {
                    if (s.input != "ERROR" && s.accumulator != null && s.operator != null) {
                        doPending(s)
                        s.accumulator = null
                        s.operator = null
                        s.freshInput = true
                    }
                }
            }
        }

        private fun displayText(raw: String): String {
            if (raw == "ERROR") return "錯誤"
            if (raw.contains("E", ignoreCase = true)) return raw

            val negative = raw.startsWith("-")
            val unsigned = if (negative) raw.drop(1) else raw
            val parts = unsigned.split(".", limit = 2)
            val intPart = parts.getOrElse(0) { "0" }.ifEmpty { "0" }
            val grouped = intPart.reversed().chunked(3).joinToString(",").reversed()
            val decimal = if (unsigned.contains(".")) "." + parts.getOrElse(1) { "" } else ""
            return (if (negative) "-" else "") + grouped + decimal
        }

        private fun pendingIntentFor(
            context: Context,
            appWidgetId: Int,
            viewId: Int,
            key: String
        ): PendingIntent {
            val intent = Intent(context, WalletCalculatorWidget::class.java).apply {
                action = ACTION_KEY
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(EXTRA_KEY, key)
            }
            val requestCode = appWidgetId * 1000 + viewId
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val options = manager.getAppWidgetOptions(appWidgetId)
            val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 260)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 360)
            val compact = minWidth < 230 || minHeight < 330

            val layoutId = if (compact) {
                R.layout.widget_wallet_compact
            } else {
                R.layout.widget_wallet_full
            }

            val views = RemoteViews(context.packageName, layoutId)
            val state = loadState(context, appWidgetId)
            views.setTextViewText(R.id.display, displayText(state.input))

            val keys = listOf(
                R.id.btn0 to "0",
                R.id.btn1 to "1",
                R.id.btn2 to "2",
                R.id.btn3 to "3",
                R.id.btn4 to "4",
                R.id.btn5 to "5",
                R.id.btn6 to "6",
                R.id.btn7 to "7",
                R.id.btn8 to "8",
                R.id.btn9 to "9",
                R.id.btnDot to ".",
                R.id.btnClear to "C",
                R.id.btnPlus to "+",
                R.id.btnMinus to "−",
                R.id.btnMultiply to "×",
                R.id.btnDivide to "÷",
                R.id.btnEquals to "="
            )

            keys.forEach { (viewId, key) ->
                views.setOnClickPendingIntent(
                    viewId,
                    pendingIntentFor(context, appWidgetId, viewId, key)
                )
            }

            manager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != ACTION_KEY) return

        val id = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val key = intent.getStringExtra(EXTRA_KEY) ?: return
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val state = loadState(context, id)
        applyKey(state, key)
        saveState(context, id, state)
        updateWidget(context, AppWidgetManager.getInstance(context), id)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { clearState(context, it) }
        super.onDeleted(context, appWidgetIds)
    }
}
