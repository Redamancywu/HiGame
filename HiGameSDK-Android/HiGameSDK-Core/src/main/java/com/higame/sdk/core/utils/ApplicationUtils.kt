package com.higame.sdk.core.utils


import android.app.Application
import android.content.Context

object ApplicationUtils {

    private var application: Application? = null

    /**
     * 初始化 Application 实例
     */
    fun init(application: Application) {
        this.application = application
    }

    /**
     * 获取全局 Application 实例
     * @throws IllegalStateException 如果未调用 init() 方法初始化 Application 实例
     */
    fun getApplication(): Application {
        return application ?: throw IllegalStateException("ApplicationUtils has not been initialized. Call init() first.")
    }

    /**
     * 获取全局 Context
     */
    fun getContext(): Context {
        return getApplication().applicationContext
    }
}