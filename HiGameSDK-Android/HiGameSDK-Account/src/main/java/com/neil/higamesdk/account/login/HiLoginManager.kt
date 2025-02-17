package com.neil.higamesdk.account.login

import android.content.Context
import com.google.gson.Gson
import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.model.LoginSuccessInfo
import com.higame.sdk.core.utils.SharedPreferencesUtils
import com.higame.sdk.core.utils.log.HiLogger

class HiLoginManager private constructor() {
    companion object {
        @Volatile
        private var instance: HiLoginManager? = null
        
        fun getInstance(): HiLoginManager =
            instance ?: synchronized(this) {
                instance ?: HiLoginManager().also { instance = it }
            }
    }
    private var initCallback:HiGameModuleInitCallback ?= null

    /**
     * 初始化回调
     */
    fun setInitCallback(initCallback: HiGameModuleInitCallback) {
        this.initCallback = initCallback
    }


    fun init(context: Context) {
        SharedPreferencesUtils.init(context)
        initCallback?.onSuccess("AutoLogin success","SharedPreferencesUtils")
    }

    fun loginAsGuest(callback: HiGameLoginCallback) {
        HiLogger.d("HiLoginManager loginAsGuest")
        try {
            // 获取或生成游客ID
            var guestId = SharedPreferencesUtils.getGuestId()
            if (guestId == null) {
                guestId = SharedPreferencesUtils.generateGuestId()
                SharedPreferencesUtils.saveGuestId(guestId)
            }

            // 生成Token和游戏账户ID
            val token = SharedPreferencesUtils.generateToken()
            SharedPreferencesUtils.saveToken(token)
            val gameAccountId = SharedPreferencesUtils.generateGameAccountId()
            SharedPreferencesUtils.saveGameAccountId(gameAccountId)
            // 创建包含登录信息的数据类实例
            val loginSuccessInfo = LoginSuccessInfo(token, gameAccountId, guestId)
            // 使用Gson将数据类实例转换为JSON字符串
            val gson = Gson()
            val jsonResult = gson.toJson(loginSuccessInfo)
            // 调用成功的回调并传递JSON结果
            callback.onSuccess("登录成功", data = jsonResult)
        } catch (e: Exception) {
            // 错误处理
            callback.onError(-1, "游客登录失败: ${e.message}")
        }
    }
    fun loginWithGoogle(callback: HiGameLoginCallback){
        HiLogger.d("HiLoginManager loginWithGoogle")
    }
}