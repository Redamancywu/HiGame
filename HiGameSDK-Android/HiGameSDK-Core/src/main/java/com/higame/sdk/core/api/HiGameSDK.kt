package com.higame.sdk.core.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.higame.sdk.core.HiGameManager
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.callback.HiGameInitCallbackAdapter
import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.model.AdType
import com.higame.sdk.core.model.LoginParams
import com.higame.sdk.core.utils.log.HiLogger

class HiGameSDK private constructor() {
    companion object {
        @Volatile
        private var instance: HiGameSDK? = null

        fun getInstance(): HiGameSDK {
            return instance ?: synchronized(this) {
                instance ?: HiGameSDK().also { instance = it }
            }
        }
    }

    /**
     * 初始化HiGameSDK
     */
    fun init(context: Context, callback: HiGameInitCallbackAdapter) {
        HiLogger.setDebug(true)
        HiLogger.d("HiGameSDK init")
        HiGameManager.initSDK(context, callback)
    }

    fun login(screen: Boolean?, params: LoginParams?, callback: HiGameLoginCallback) {
        HiGameManager.login(screen, params, callback)
    }

    fun onResume() {
        HiGameManager.onResume()
    }
    fun setActivity(activity: Activity){
        HiLogger.d("HiGameSDK setActivity :${activity.javaClass.simpleName}")
        HiGameManager.setActivity(activity)
    }
    fun onNewIntent(intent: Intent) {
        HiGameManager.onNewIntent(intent)
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        HiGameManager.onActivityResult(requestCode, resultCode, data)
    }
    fun showAd(adType: AdType){
        HiGameManager.showAd(adType)
    }
    fun loadAd(){
        HiGameManager.loadAd()
    }
    fun closeAd(adType: AdType){
        HiGameManager.closeAd(adType)
    }
    fun setAdListener(callback:HiGameAdCallback){
        HiGameManager.setAdCallback(callback)
    }
}



