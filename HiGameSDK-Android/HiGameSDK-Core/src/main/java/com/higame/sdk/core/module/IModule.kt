package com.higame.sdk.core.module

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig

interface IModule {
    /**
     * 获取模块名称
     *
     * @return 模块名称字符串
     */
    fun getModuleName(): String

    /**
     * 初始化模块
     *
     * @param context 上下文，用于访问应用程序资源和类
     * @param config  模块配置信息，包含初始化所需的各种参数
     */
    fun init(context: Context, config: HiGameConfig)

    /**
     * 设置Activity
     *
     * @param activity 当前的Activity，用于在模块中进行UI操作或生命周期管理
     */
    fun setActivity(activity: Activity)

    /**
     * 设置回调接口
     *
     * @param callback 模块初始化回调接口，用于通知模块初始化结果
     */
    fun setCallback(callback: HiGameModuleInitCallback)

    /**
     * 模块暂停时调用
     */
    fun onPause()

    /**
     * 模块停止时调用
     */
    fun onStop()

    /**
     * 模块销毁时调用
     */
    fun onDestroy()

    /**
     * 模块重新显示时调用
     */
    fun onResume()

    /**
     * 模块处理活动结果时调用
     *
     * @param requestCode 从startActivityForResult()中返回的请求码
     * @param resultCode  活动返回的结果码
     * @param data        活动返回的Intent数据
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    /**
     * 模块接收到新的Intent时调用
     *
     * @param intent 新的Intent数据
     */
    fun onNewIntent(intent: Intent?)


}