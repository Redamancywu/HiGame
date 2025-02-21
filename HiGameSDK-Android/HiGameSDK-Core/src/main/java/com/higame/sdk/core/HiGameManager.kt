package com.higame.sdk.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.callback.HiGameInitCallback
import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.model.AdType
import com.higame.sdk.core.model.LoginParams
import com.higame.sdk.core.module.IAccountModule
import com.higame.sdk.core.module.IAdModule
import com.higame.sdk.core.module.IModule
import com.higame.sdk.core.module.IPaymentModule
import com.higame.sdk.core.utils.ApplicationUtils
import com.higame.sdk.core.utils.ConfigLoader.loadConfig
import com.higame.sdk.core.utils.ContextUtils
import com.higame.sdk.core.utils.log.HiLogger
import java.util.ServiceLoader

object HiGameManager {

    // SDK 初始化状态
    private var isInitialized = false
    private var initializationError: String? = null
    private var config: HiGameConfig? = null

    // 使用 lazy 委托来延迟加载插件列表，直到第一次访问时才加载
    private val plugins: List<IModule> by lazy {
        val loadedPlugins = ServiceLoader.load(IModule::class.java).toList()
        HiLogger.d("Loaded plugins: ${loadedPlugins.map { it.getModuleName() }}")
        loadedPlugins
    }

    private var initCallback: HiGameInitCallback? = null

    // 全局成功和失败数据
    private val successDataMap = mutableMapOf<String, Any?>()
    private val errorDataMap = mutableMapOf<String, String>()
    private val successDataList = mutableListOf<Map<String, Any?>>()

    /**
     * 获取 SDK 初始化状态
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * 获取初始化错误信息
     */
    fun getInitializationError(): String? = initializationError

    /**
     * 插件初始化
     */
    // 添加模块初始化状态跟踪
    private val moduleInitStatus = mutableMapOf<String, Boolean>()
    private var totalModules = 0
    private var initializedModules = 0

    private fun initialAll() {
        // 重置状态
        moduleInitStatus.clear()
        successDataMap.clear()
        errorDataMap.clear()
        successDataList.clear()
        initializedModules = 0
        var hasError = false

        // 获取总模块数
        totalModules = plugins.size
        HiLogger.d("Total modules to initialize: $totalModules")

        plugins.forEach { plugin ->
            try {
                val pluginName = plugin.getModuleName()
                HiLogger.d("Initializing plugin: $pluginName")
                moduleInitStatus[pluginName] = false

                plugin.setCallback(object : HiGameModuleInitCallback {
                    override fun onSuccess(message: String, data: Any?) {
                        HiLogger.d("Received onSuccess callback with data: $data for plugin: $pluginName")
                        synchronized(moduleInitStatus) {
                            if (!moduleInitStatus[pluginName]!!) {
                                moduleInitStatus[pluginName] = true
                                successDataMap[pluginName] = data
                                successDataList.add(mapOf("message" to message, "data" to data))
                                initCallback?.onModuleLoaded(pluginName, true, message)
                                initializedModules++
                                checkInitializationComplete()
                            }
                        }
                    }

                    override fun onFailed(code: Int, msg: String) {
                        synchronized(moduleInitStatus) {
                            if (!moduleInitStatus[pluginName]!!) {
                                hasError = true
                                moduleInitStatus[pluginName] = true
                                errorDataMap[pluginName] = "[Error Code: $code] $msg"
                                HiLogger.e("Failed to initialize plugin: $pluginName: $msg (Code: $code)")
                                initCallback?.onModuleLoaded(pluginName, false, msg)
                                initializedModules++
                                checkInitializationComplete()
                            }
                        }
                    }
                })
                plugin.init(ContextUtils.getContext(), loadConfig(ContextUtils.getContext()))
            } catch (e: Exception) {
                hasError = true
                val pluginName = plugin.getModuleName()
                val errorMessage = e.message ?: "Unknown error"
                synchronized(moduleInitStatus) {
                    if (!moduleInitStatus[pluginName]!!) {
                        moduleInitStatus[pluginName] = true
                        errorDataMap[pluginName] = "[Exception] $errorMessage"
                        HiLogger.e("Exception during initialization of plugin: $pluginName", e)
                        initCallback?.onModuleLoaded(pluginName, false, errorMessage)
                        initializedModules++
                        checkInitializationComplete()
                    }
                }
            }
        }
    }

    private fun checkInitializationComplete() {
        HiLogger.d("Checking initialization status: $initializedModules/$totalModules modules initialized")

        if (initializedModules == totalModules) {
            val hasError = errorDataMap.isNotEmpty()
            if (hasError) {
                initializationError = "Some plugins failed to initialize"
                HiLogger.e(initializationError!!)
                initCallback?.onError(
                    code = -1,
                    message = "SDK Core ${initializationError!!}",
                    data = errorDataMap
                )
                return
            }

            // 所有模块都初始化成功
            isInitialized = true
            HiLogger.d("SDK Core isInitialized :${isInitialized()}")
            initializationError = null
            val successJson = successDataList.map { it }.toMutableList()
            initCallback?.onInitComplete()
            HiLogger.d("SDK Core initialized successfully :${successJson}")
            initCallback?.onSuccess(
                message = "All modules initialized successfully",
                data = successJson
            )
        }
    }

    /**
     * 获取所有插件名称
     */
    fun getPluginNames(): List<String> {
        return plugins.map { it.getModuleName() }
    }

    /**
     * 初始化 SDK
     */
    fun initSDK(context: Context, callback: HiGameInitCallback) {
        HiGameLifecycle.init(ApplicationUtils.getApplication())
        if (isInitialized) {
            callback.onInitComplete()
            return
        }

        this.initCallback = callback
        initializationError = null

        try {
            // 通知初始化开始
            callback.onInitStart()

            // 加载并初始化所有插件
            initialAll()

            // 加载 SDK 配置信息
            try {
                val config = loadConfig(context)
                callback.onConfigLoaded(config)
                // 使用 Gson 将配置对象转换为格式化的 JSON 字符串
                val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                val configJson = gson.toJson(config)
                HiLogger.d("SDK配置信息:\n$configJson")
            } catch (e: Exception) {
                HiLogger.e("Failed to load config", e)
                initializationError = "Config loading failed: ${e.message}"
                callback.onModuleLoaded("Config", false, e.message ?: "Unknown error")
                return
            }
        } catch (e: Exception) {
            HiLogger.e("SDK initialization failed", e)
            initializationError = e.message ?: "Unknown error"
            callback.onModuleLoaded("SDK Core", false, e.message ?: "Unknown error")
        }
    }

    /**
     * 登录方法
     */
    fun login(screen: Boolean?, params: LoginParams?, callback: HiGameLoginCallback) {
        if (!isInitialized()) {
            val error = getInitializationError() ?: "SDK has not been initialized"
            HiLogger.e(error)
            callback.onError(-1, "SDK Core", error)
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("Logging in with plugin: ${plugin.getModuleName()}")
                if (plugin is IAccountModule) {
                    plugin.login(screen, params, callback)
                }
            } catch (e: Exception) {
                HiLogger.e("Failed to perform login with plugin: ${plugin.getModuleName()}", e)
                callback.onError(-1, plugin.getModuleName(), e.message ?: "Unknown error")
            }
        }
    }

    fun showAd(adType: AdType) {
        plugins.forEach { plugin ->
            try {
                HiLogger.d("showAd with plugin: ${plugin.getModuleName()}")
                if (plugin is IAdModule) {
                    plugin.showAd(adType)
                }
            } catch (e: Exception) {
                HiLogger.e("Failed to perform showAd with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun loadAd() {
        plugins.forEach { plugin ->
            try {
                HiLogger.d("loadAd with plugin: ${plugin.getModuleName()}")
                if (plugin is IAdModule) {
                    plugin.loadAd()
                }
            } catch (e: Exception) {
                HiLogger.e("Failed to perform loadAd with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun setAdCallback(callback: HiGameAdCallback) {
        plugins.forEach { plugin ->
            try {
                HiLogger.d("setAdCallback with plugin: ${plugin.getModuleName()}")
                if (plugin is IAdModule) {
                    plugin.setAdCallback(callback)
                }
            } catch (e: Exception) {
                HiLogger.e(
                    "Failed to perform setAdCallback with plugin: ${plugin.getModuleName()}",
                    e
                )
            }
        }
    }

    fun closeAd(adType: AdType) {
        plugins.forEach { plugin ->
            try {
                HiLogger.d("closeAd with plugin: ${plugin.getModuleName()}")
                if (plugin is IAdModule) {
                    HiLogger.d("HiGameManager closeAd")
                    plugin.closeAd(adType)
                }
            } catch (e: Exception) {
                HiLogger.e("Failed to perform closeAd with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    /**
     * 处理 Activity 生命周期方法
     */
    fun onResume() {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理onResume")
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("onResume with plugin: ${plugin.getModuleName()}")
                plugin.onResume()
            } catch (e: Exception) {
                HiLogger.e("Failed to perform onResume with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun onPause() {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理onPause")
            return
        }
        plugins.forEach { plugin ->
            try {
                HiLogger.d("onPause with plugin: ${plugin.getModuleName()}")
                plugin.onPause()
            } catch (e: Exception) {
                HiLogger.e("Failed to perform onPause with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun onStop() {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理onStop")
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("onStop with plugin: ${plugin.getModuleName()}")
                plugin.onStop()
            } catch (e: Exception) {
                HiLogger.e("Failed to perform onStop with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun onDestroy() {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理onDestroy")
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("onDestroy with plugin: ${plugin.getModuleName()}")
                plugin.onDestroy()
            } catch (e: Exception) {
                HiLogger.e("Failed to perform onDestroy with plugin: ${plugin.getModuleName()}", e)
            }
        }
    }

    fun setActivity(activity: Activity) {
        HiLogger.d("HiGameManager setActivity($activity")
//        if (!isInitialized()) {
//            HiLogger.e("SDK未初始化，无法设置Activity")
//            return
//        }
        plugins.forEach { plugin ->
            try {
                HiLogger.d("setActivity with plugin: ${plugin.getModuleName()}")
                plugin.setActivity(activity)
            } catch (e: Exception) {
                HiLogger.e(
                    "Failed to perform setActivity with plugin: ${plugin.getModuleName()}",
                    e
                )
            }
        }
    }

    fun onNewIntent(intent: Intent) {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理新的Intent数据")
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("onNewIntent with plugin: ${plugin.getModuleName()}")
                plugin.onNewIntent(intent)
            } catch (e: Exception) {
                HiLogger.e(
                    "Failed to perform onNewIntent with plugin: ${plugin.getModuleName()}",
                    e
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!isInitialized()) {
            HiLogger.e("SDK未初始化，无法处理onActivityResult")
            return
        }

        plugins.forEach { plugin ->
            try {
                HiLogger.d("onActivityResult with plugin: ${plugin.getModuleName()}")
                plugin.onActivityResult(requestCode, resultCode, data)
            } catch (e: Exception) {
                HiLogger.e(
                    "Failed to perform onActivityResult with plugin: ${plugin.getModuleName()}",
                    e
                )
            }
        }
    }
}