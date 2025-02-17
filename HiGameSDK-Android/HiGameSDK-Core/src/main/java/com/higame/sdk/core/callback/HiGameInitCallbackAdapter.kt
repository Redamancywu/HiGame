package com.higame.sdk.core.callback

/**
 * HiGame SDK 初始化回调接口的默认实现
 * 用户可以继承此类，选择性地重写所需的回调方法
 */
open class HiGameInitCallbackAdapter : HiGameInitCallback {
    override fun onInitStart() {}

    override fun onInitComplete() {}

    override fun onConfigLoaded(config: Any) {}

    override fun onModuleLoaded(moduleName: String, isLoaded: Boolean, message: String) {}

    override fun onSuccess(message: String, data: Any?) {}

    override fun onError(code: Int, message: String, data: Any?) {}
}