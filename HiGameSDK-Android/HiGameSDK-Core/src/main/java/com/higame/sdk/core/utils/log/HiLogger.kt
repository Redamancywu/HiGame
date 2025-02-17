package com.higame.sdk.core.utils.log

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * HiGameSDK日志工具类
 * 支持日志等级、时间戳和格式化输出
 */
class HiLogger private constructor() {
    companion object {
        private const val DEFAULT_TAG = "HiGameSDK"
        private var isDebug = true
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

        // 边框样式定义
        private const val TOP_BORDER =    "┌───────────────────────────────────────────────────────────────────────────────┐"
        private const val BOTTOM_BORDER = "└───────────────────────────────────────────────────────────────────────────────┘"
        private const val SEPARATOR =     "├───────────────────────────────────────────────────────────────────────────────┤"
        private const val VERTICAL_LINE = "│"

        /**
         * 设置是否开启调试模式
         */
        fun setDebug(debug: Boolean) {
            isDebug = debug
        }

        /**
         * 格式化日志消息
         */
        private fun formatLog(level: String, tag: String, message: String): String {
            val time = dateFormat.format(Date())
            val header = "$TOP_BORDER\n$VERTICAL_LINE ${time} [$level] $tag"
            val messageLines = message.split("\n")
            val formattedMessage = messageLines.joinToString("\n") { "$VERTICAL_LINE $it" }
            return "$header\n$SEPARATOR\n$formattedMessage\n$BOTTOM_BORDER"
        }

        /**
         * 添加颜色到日志消息
         */
        private fun colorize(color: String, message: String): String {
            return message
        }

        /**
         * Debug级别日志
         */
        fun d(message: String, tag: String = DEFAULT_TAG) {
            if (isDebug) {
                val formattedMessage = formatLog("DEBUG", tag, message)
                Log.d(tag, formattedMessage)
            }
        }

        /**
         * Info级别日志
         */
        fun i(message: String, tag: String = DEFAULT_TAG) {
            val formattedMessage = formatLog("INFO", tag, message)
            Log.i(tag, formattedMessage)
        }

        /**
         * Warn级别日志
         */
        fun w(message: String, tag: String = DEFAULT_TAG) {
            val formattedMessage = formatLog("WARN", tag, message)
            Log.w(tag, formattedMessage)
        }

        /**
         * Error级别日志
         */
        fun e(message: String, tag: String = DEFAULT_TAG) {
            val formattedMessage = formatLog("ERROR", tag, message)
            Log.e(tag, formattedMessage)
        }

        /**
         * Error级别日志，带异常信息
         */
        fun e(message: String, throwable: Throwable, tag: String = DEFAULT_TAG) {
            val formattedMessage = formatLog("ERROR", tag, "$message\n${Log.getStackTraceString(throwable)}")
            Log.e(tag, formattedMessage)
        }
    }
}