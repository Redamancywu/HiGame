package com.higame.sdk.core.callback

/**
 * HiGame SDK 回调接口基类
 */
interface HiGameCallback {
    /**
     * 成功回调
     * @param code 成功码
     * @param message 成功信息
     * @param data 返回数据
     */
    fun onSuccess(message: String = "", data: Any? = null)

    /**
     * 失败回调
     * @param code 错误码
     * @param message 错误信息
     * @param data 错误数据
     */
    fun onError(code: Int, message: String, data: Any? = null)
}