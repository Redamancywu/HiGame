package com.higame.sdk.core.callback

interface HiGameModuleInitCallback {
    /**
     * 初始化成功
     */
    fun onSuccess(message:String = "",data : Any?)

    /**
     * 初始化失败
     */
    fun onFailed(code: Int, msg: String)

}