package com.neil.higamesdk.ad

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTCustomController
import com.bytedance.sdk.openadsdk.mediation.init.IMediationPrivacyConfig
import com.bytedance.sdk.openadsdk.mediation.init.MediationConfig
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig
import com.google.auto.service.AutoService
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.model.AdInfo
import com.higame.sdk.core.model.AdType
import com.higame.sdk.core.module.IAdModule
import com.higame.sdk.core.module.IModule
import com.higame.sdk.core.utils.JsonUtils
import com.higame.sdk.core.utils.log.HiLogger
import com.neil.higamesdk.ad.banner.HiGameTTBannerAd
import com.neil.higamesdk.ad.banner.HiGameTTInterAd

@AutoService(IModule::class)
class HiGameTTAdSDK : IAdModule {
    private lateinit var moduleInitCallback: HiGameModuleInitCallback
    private var adUnitIds: String? = null
    //private lateinit var type: AdType
    override fun loadAd() {
        HiGameTTInterAd.getInstance().loadAdInters()
        HiGameTTBannerAd.getInstance().loadBannerAd()
    }

    override fun showAd(adType: AdType) {
        when (adType) {
            AdType.BANNER -> {
                HiGameTTBannerAd.getInstance().showBannerAd()
            }

            AdType.NATIVE -> {}
            AdType.INTERSTITIAL -> {
                HiLogger.d("HiGameTTAdSDK showAdInters")
                HiGameTTInterAd.getInstance().showAdInters()
            }

            AdType.REWARDED -> {}
            AdType.SPLASH -> {}
            AdType.DRAWER -> {}
            else->{HiLogger.d("Unsupported ad type for TTAdSDK showAd")}
        }
    }

    override fun setAdCallback(callback: HiGameAdCallback) {
        HiLogger.d("HiGameTTAdSDK setAdCallback :${callback}")
        HiGameTTBannerAd.getInstance().setAdCallback(callback)
        HiGameTTInterAd.getInstance().setAdCallback(callback)
//        when (type) {
//            AdType.BANNER -> {
//                HiGameTTBannerAd.getInstance().setAdCallback(callback)
//            }
//
//            AdType.INTERSTITIAL -> {
//                HiGameTTInterAd.getInstance().setAdCallback(callback)
//            }
//
//            AdType.REWARDED -> {}
//            AdType.SPLASH -> {}
//            AdType.DRAWER -> {}
//            AdType.NATIVE -> {}
//            else ->{HiLogger.d("Unsupported ad type for TTAdSDK SetAdCallback")}
    //    }

    }

    override fun getAdInfo(): AdInfo? {
        return adUnitIds?.let {
            AdInfo(
                adType = 1,
                adId = it,
                adName = "穿山甲广告",
                adDesc = "穿山甲广告",
                adStyleType = 2

            )
        }
    }

    override fun closeAd(adType: AdType) {
        when (adType) {
            AdType.BANNER -> {
                HiGameTTBannerAd.getInstance().destroyBannerAd()
            }

            AdType.INTERSTITIAL -> {
                HiGameTTInterAd.getInstance().destroy()
            }

            AdType.REWARDED -> {}
            AdType.SPLASH -> {}
            AdType.DRAWER -> {}
            AdType.NATIVE -> {}
            else->HiLogger.d("Unsupported ad type for TTAdSDK")
        }

    }

    override fun getModuleName(): String {
        return "HiGameTTAdSDK"
    }

    override fun init(context: Context, config: HiGameConfig) {
        adUnitIds = config.features?.ads?.adUnitIds?.adPosId
        HiLogger.d("HiGameTTAdSDK init adPosId:${adUnitIds}")
        if (adUnitIds == null) {
            moduleInitCallback.onFailed(5001, "adUnitIds is null Please configure the ad id")
            return
        }
        TTAdSdk.init(context, buildConfig(context))

        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                HiLogger.d("HiGameTTAdSDK init success")
                HiLogger.d("HiGameTTAdSDK moduleInitCallback:${moduleInitCallback}")
                moduleInitCallback.onSuccess("TTAdSDK初始化成功", TTAdSdk.isSdkReady())
                HiGameTTBannerAd.getInstance().init(context, config)
                HiGameTTInterAd.getInstance().init(context, config)
                loadAd()
            }

            override fun fail(code: Int, message: String?) {
                if (message != null) {
                    moduleInitCallback.onFailed(code, message)
                }
            }
        })
    }

    private fun buildConfig(context: Context): TTAdConfig {
        val jsonObject = JsonUtils.getJsonObjectFromAssets(context, "report_1739437291.json")
        return TTAdConfig.Builder()
            .appId(adUnitIds)
            .customController(getTTCustomController())
            .setMediationConfig(MediationConfig.Builder().setCustomLocalConfig(jsonObject).build())
            .useMediation(true)
            .supportMultiProcess(true)
            .build()
    }

    private fun getTTCustomController(): TTCustomController {
        return object : TTCustomController() {
            override fun isCanUseAndroidId(): Boolean {
                HiLogger.d("HiGameTTAdSDK isCanUseAndroidId")
                return super.isCanUseAndroidId()
            }

            override fun getMacAddress(): String? {
                HiLogger.d("HiGameTTAdSDK getMacAddress")
                return super.getMacAddress()
            }

            override fun isCanUseWriteExternal(): Boolean {
                HiLogger.d("HiGameTTAdSDK isCanUseWriteExternal")
                return super.isCanUseWriteExternal()
            }

            override fun getDevOaid(): String? {
                HiLogger.d("HiGameTTAdSDK getDevOaid")
                return super.getDevOaid()
            }

            override fun getAndroidId(): String? {
                HiLogger.d("HiGameTTAdSDK getAndroidId")
                return super.getAndroidId()
            }

            override fun getMediationPrivacyConfig(): IMediationPrivacyConfig? {
                return object : MediationPrivacyConfig() {
                    override fun isLimitPersonalAds(): Boolean {
                        HiLogger.d("HiGameTTAdSDK isLimitPersonalAds")
                        return super.isLimitPersonalAds()
                    }

                    override fun isProgrammaticRecommend(): Boolean {
                        HiLogger.d("HiGameTTAdSDK isProgrammaticRecommend")
                        return super.isProgrammaticRecommend()
                    }
                }

            }
        }
    }

    override fun setActivity(activity: Activity) {
//        when (type) {
//            AdType.BANNER -> {
//                HiGameTTBannerAd.getInstance().setActivity(activity)
//            }
//
//            AdType.INTERSTITIAL -> {
//                HiGameTTInterAd.getInstance().setActivity(activity)
//            }
//
//            AdType.REWARDED -> {}
//            AdType.SPLASH -> {}
//            AdType.DRAWER -> {}
//            AdType.NATIVE -> {}
//        }
        HiGameTTBannerAd.getInstance().setActivity(activity)
        HiGameTTInterAd.getInstance().setActivity(activity)
    }

    override fun setCallback(callback: HiGameModuleInitCallback) {
        HiLogger.d("HiGameTTAdSDK init callback:${callback}")
        this.moduleInitCallback = callback

    }

    override fun onResume() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun onNewIntent(intent: Intent?) {

    }
}