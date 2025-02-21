package com.higame.sdk.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2.TRIM_MEMORY_COMPLETE
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW
import android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
import android.os.Build
import android.os.Bundle
import com.higame.sdk.core.utils.log.HiLogger

@SuppressLint("StaticFieldLeak")
object HiGameLifecycle : Application.ActivityLifecycleCallbacks {

    private var application: Application? = null
    private var currentActivity: Activity? = null

    fun init(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(this)
        HiLogger.d("HiGameLifecycle initialized and registered")
    }

    fun observeActivity() {
        currentActivity?.let { HiGameManager.setActivity(it) }
    }

    /**
     * 监听应用终止（仅在模拟器中有效）
     */
    fun onTerminate() {

        HiLogger.d("onTerminate() - App is being killed (only in emulators).")
        // 获取当前设备信息
        val deviceInfo = "Device: ${Build.MODEL}, SDK: ${Build.VERSION.SDK_INT}"
        HiLogger.d("onTerminate() - $deviceInfo")
    }

    /**
     * 监听系统内存不足
     */
    fun onLowMemory() {

        HiLogger.d("onLowMemory() - System is running low on memory.")
        // 获取当前可用内存信息（需要结合 ActivityManager 或其他工具）
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // 转换为 MB
        val freeMemory = runtime.freeMemory() / (1024 * 1024) // 转换为 MB
        HiLogger.d("onLowMemory() - Max Memory: ${maxMemory}MB, Free Memory: ${freeMemory}MB")
    }

    /**
     * 监听内存优化
     */
    fun onTrimMemory(level: Int) {

        HiLogger.d("onTrimMemory() - Memory trim level: $level")

        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {
                HiLogger.d("App moved to background, potential UI resources can be released.")
            }

            TRIM_MEMORY_RUNNING_LOW, TRIM_MEMORY_RUNNING_CRITICAL -> {
                HiLogger.d("App is running low on memory, consider optimizing resources.")
                // 获取当前内存状态
                val runtime = Runtime.getRuntime()
                val maxMemory = runtime.maxMemory() / (1024 * 1024) // 转换为 MB
                val freeMemory = runtime.freeMemory() / (1024 * 1024) // 转换为 MB
                HiLogger.d("onTrimMemory() - Max Memory: ${maxMemory}MB, Free Memory: ${freeMemory}MB")
            }

            TRIM_MEMORY_COMPLETE -> {
                HiLogger.d("App is in the background and should release all non-critical resources.")
            }

            else -> {
                HiLogger.d("Unknown trim level: $level")
            }
        }
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        HiLogger.d("onActivityCreated: ${activity.localClassName}")
        currentActivity = activity
        HiGameManager.setActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        HiLogger.d("onActivityStarted: ${activity.localClassName}")
    }

    override fun onActivityResumed(activity: Activity) {
        HiLogger.d("onActivityResumed: ${activity.localClassName}")
        currentActivity = activity
        HiGameManager.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        HiLogger.d("onActivityPaused: ${activity.localClassName}")
        HiGameManager.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        HiLogger.d("onActivityStopped: ${activity.localClassName}")
        HiGameManager.onStop()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        HiLogger.d("onActivityDestroyed: ${activity.localClassName}")
        if (currentActivity == activity) {
            currentActivity = null
        }
        HiGameManager.onDestroy()
    }
}
