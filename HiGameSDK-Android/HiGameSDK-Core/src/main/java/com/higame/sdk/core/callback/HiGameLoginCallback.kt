package com.higame.sdk.core.callback

/**
 * HiGame SDK 登录回调接口
 */
interface HiGameLoginCallback : HiGameCallback {
    /**
     * 登录取消回调
     */
    fun onCancel()
}