package com.higame.sdk.core.module

import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.model.AdInfo
import com.higame.sdk.core.model.AdType

interface IAdModule : IModule {

    /**
     * 加载广告
     */
    fun loadAd()


    /**
     * 展示指定类型的广告
     * @param adType 广告类型
     */
    fun showAd(adType: AdType)

    /**
     * 设置广告回调
     * @param callback 广告回调接口
     */
    fun setAdCallback(callback: HiGameAdCallback)


    /**
     * 获取广告信息
     */
    fun getAdInfo(): AdInfo?

    /**
     * 关闭广告
     */
    fun closeAd(adType: AdType)
}