package com.higame.sdk.core.module

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig

interface IModule {
    fun getModuleName():String //模块名称
    fun init(context: Context, config: HiGameConfig) //初始化
    fun setActivity(activity:Activity)
    fun setCallback(callback:HiGameModuleInitCallback)
    fun onResume()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun onNewIntent(intent: Intent?)


}