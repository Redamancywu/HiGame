package com.higame.sdk.core.utils

import android.content.Context
import java.lang.IllegalStateException

/**
 * Context工具类
 * 负责管理全局Context对象
 */
object ContextUtils {
    private var applicationContext: Context? = null

    /**
     * 初始化Context
     * @param context Android上下文
     * @throws IllegalArgumentException 当context为null时抛出
     */
    fun init(context: Context) {
        if (context == null) {
            throw IllegalArgumentException("Context must not be null")
        }
        applicationContext = context.applicationContext
    }

    /**
     * 获取全局Context对象
     * @return Context 全局Context对象
     * @throws IllegalStateException 当Context未初始化时抛出
     */
    fun getContext(): Context {
        return applicationContext ?: throw IllegalStateException(
            "Context not initialized. Call ContextUtils.init(context) first."
        )
    }

    /**
     * 清理Context引用
     * 通常在应用退出时调用
     */
    fun clear() {
        applicationContext = null
    }
}