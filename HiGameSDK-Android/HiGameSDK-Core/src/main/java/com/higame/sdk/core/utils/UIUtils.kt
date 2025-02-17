package com.higame.sdk.core.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout

object UIUtils {

    /**
     * 获取屏幕宽度（单位：dp）
     */
    fun getScreenWidthDp(context: Context): Float {
        val scale = context.resources.displayMetrics.density
        val width = context.resources.displayMetrics.widthPixels
        return width.toFloat() / (if (scale <= 0) 1f else scale) + 0.5f
    }

    /**
     * 全面屏、刘海屏适配，获取屏幕高度
     */
    fun getHeight(activity: Activity): Int {
        hideBottomUIMenu(activity)
        val realHeight = getRealHeight(activity)
        return if (hasNotchScreen(activity)) {
            px2dip(activity, realHeight - getStatusBarHeight(activity))
        } else {
            px2dip(activity, realHeight.toFloat())
        }
    }

    /**
     * 隐藏底部导航栏菜单
     */
    fun hideBottomUIMenu(activity: Activity) {
        if (activity == null) return
        try {
            if (Build.VERSION.SDK_INT in 12..18) { // lower api
                activity.window.decorView.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                val decorView = activity.window.decorView
                val uiOptions = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                or View.SYSTEM_UI_FLAG_IMMERSIVE
                        )
                decorView.systemUiVisibility = uiOptions
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取屏幕真实高度（不包含下方虚拟导航栏）
     */
    fun getRealHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm)
        } else {
            display.getMetrics(dm)
        }
        return dm.heightPixels
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Float {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId).toFloat()
        } else {
            0f
        }
    }
    /**
     * 将像素值转换为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / (if (scale <= 0) 1f else scale) + 0.5f).toInt()
    }

    /**
     * 将 dp 转换为像素值
     */
    fun dp2px(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /**
     * 判断是否是刘海屏
     */
    fun hasNotchScreen(activity: Activity): Boolean {
        return isAndroidPHasNotch(activity)
                || getInt("ro.miui.notch", activity) == 1
                || hasNotchAtHuawei(activity)
                || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity)
    }

    /**
     * Android P 刘海屏判断
     */
    fun isAndroidPHasNotch(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = activity.window.decorView.rootWindowInsets
            val displayCutout = windowInsets?.displayCutout
            return displayCutout != null
        }
        return false
    }

    /**
     * 小米刘海屏判断
     */
    fun getInt(key: String, activity: Activity): Int {
        if (isMiui()) {
            try {
                val systemProperties = activity.classLoader.loadClass("android.os.SystemProperties")
                val getInt = systemProperties.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
                return getInt.invoke(systemProperties, key, 0) as Int
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return 0
    }

    /**
     * 华为刘海屏判断
     */
    fun hasNotchAtHuawei(context: Context): Boolean {
        return try {
            val hwNotchSizeUtil = context.classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val method = hwNotchSizeUtil.getMethod("hasNotchInScreen")
            method.invoke(hwNotchSizeUtil) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    /**
     * VIVO 刘海屏判断
     */
    fun hasNotchAtVivo(context: Context): Boolean {
        return try {
            val ftFeature = context.classLoader.loadClass("android.util.FtFeature")
            val method = ftFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            method.invoke(ftFeature, VIVO_NOTCH) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    /**
     * OPPO 刘海屏判断
     */
    fun hasNotchAtOPPO(context: Context): Boolean {
        val temp = "com.kllk.feature.screen.heteromorphism"
        val name = getKllkDecryptString(temp)
        return context.packageManager.hasSystemFeature(name)
    }

    /**
     * 判断是否是 MIUI 系统
     */
    fun isMiui(): Boolean {
        return try {
            Class.forName("miui.os.Build") != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 解密字符串（用于 OPPO）
     */
    fun getKllkDecryptString(encryptionString: String): String {
        if (encryptionString.isEmpty()) return ""
        val decryptCapitalized = "O" + "P" + "P" + "O"
        val decrypt = "o" + "p" + "p" + "o"
        return if (encryptionString.contains("KLLK")) {
            encryptionString.replace("KLLK", decryptCapitalized)
        } else if (encryptionString.contains("kllk")) {
            encryptionString.replace("kllk", decrypt)
        } else {
            ""
        }
    }

    /**
     * 设置 View 的宽高
     */
    fun setViewSize(view: View, width: Int, height: Int) {
        val layoutParams = when (val parent = view.parent) {
            is FrameLayout -> view.layoutParams as FrameLayout.LayoutParams
            is RelativeLayout -> view.layoutParams as RelativeLayout.LayoutParams
            is LinearLayout -> view.layoutParams as LinearLayout.LayoutParams
            else -> throw IllegalArgumentException("Unsupported parent type")
        }
        layoutParams.width = width
        layoutParams.height = height
        view.layoutParams = layoutParams
        view.requestLayout()
    }

    /**
     * 获取屏幕宽度（单位：像素）
     */
    fun getScreenWidthInPx(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度（单位：像素）
     */
    fun getScreenHeightInPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取屏幕高度（包括状态栏）
     */
    fun getScreenHeight(context: Context): Int {
        return getScreenHeightInPx(context) + getStatusBarHeight(context).toInt()
    }

    /**
     * 从父布局中移除 View
     */
    fun removeFromParent(view: View?) {
        if (view != null && view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    /**
     * 获取全面屏宽高
     */
    fun getScreenSize(context: Context): IntArray {
        if (context == null) return intArrayOf(0, 0)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm)
        } else {
            display.getMetrics(dm)
        }
        return intArrayOf(dm.widthPixels, dm.heightPixels)
    }

    private const val VIVO_NOTCH = 0x00000020 // 是否有刘海
}