package com.higame.sdk.core.callback

/**
 * HiGame SDK 初始化回调接口
 */
interface HiGameInitCallback : HiGameCallback {
    /**
     * SDK初始化开始
     */
    fun onInitStart()

    /**
     * SDK初始化完成
     */
    fun onInitComplete()

    /**
     * SDK配置加载完成
     * @param config SDK配置信息
     */
    fun onConfigLoaded(config: Any)

    /**
     * SDK模块加载状态
     * @param moduleName 模块名称
     * @param isLoaded 是否加载成功
     * @param message 加载状态描述
     */
    fun onModuleLoaded(moduleName: String, isLoaded: Boolean, message: String = "")
}