package com.neil.higamesdk.ad.banner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdNative.FullScreenVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.utils.log.HiLogger

class HiGameTTInterAd private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: HiGameTTInterAd? = null

        fun getInstance(): HiGameTTInterAd =
            instance ?: synchronized(this) {
                instance ?: HiGameTTInterAd().also { instance = it }
            }
    }

    private lateinit var currentActivity: Activity
    private lateinit var adCallback: HiGameAdCallback
    private var adNativeLoader: TTAdNative? = null
    private var interPosId: String? = null
    private var adSlot: AdSlot? = null
    private var mTTFullScreenVideoAd: TTFullScreenVideoAd? = null

    /**
     * 设置当前 Activity
     */
    fun setActivity(activity: Activity) {
        this.currentActivity = activity
    }

    /**
     * 设置广告回调
     */
    fun setAdCallback(callback: HiGameAdCallback) {
        this.adCallback = callback
    }

    /**
     * 初始化
     */
    fun init(context: Context, config: HiGameConfig) {
        if (!::adCallback.isInitialized) {
            throw IllegalStateException("Callback must be set before initializing ads.")
        }
        interPosId = config.features?.ads?.adUnitIds?.interstitial
        HiLogger.d("interPosId:${interPosId}")
        this.adSlot = AdSlot.Builder()
            .setCodeId(interPosId)
            .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
            .setMediationAdSlot(MediationAdSlot.Builder()
                .setMuted(true)//是否静音
                .setVolume(0.7f)//设置音量
                .setBidNotify(true)//竞价结果通知
                .build())
            .build()
        HiLogger.d("TTAdSdk.getAdManager().createAdNative(currentActivity):${TTAdSdk.getAdManager().createAdNative(currentActivity)} ,currentActivity:${currentActivity} ")
        this.adNativeLoader = TTAdSdk.getAdManager().createAdNative(currentActivity)
    }

    fun loadAdInters() {
        this.adNativeLoader?.loadFullScreenVideoAd(adSlot,
            object : TTAdNative.FullScreenVideoAdListener {
                override fun onError(code: Int, message: String?) {
                    HiLogger.e("load interstitial error:${message} , code:${code}")
                    adCallback.onAdFailedToLoad(code, message)
                }

                override fun onFullScreenVideoAdLoad(ad: TTFullScreenVideoAd?) {
                    HiLogger.d("load interstitial success:${ad}")
                    adCallback.onAdLoaded("interstitial success")
                    mTTFullScreenVideoAd = ad
                }

                override fun onFullScreenVideoCached() {

                }

                override fun onFullScreenVideoCached(ad: TTFullScreenVideoAd?) {
                    mTTFullScreenVideoAd = ad
                }

            })
    }

    fun showAdInters() {
        if (mTTFullScreenVideoAd == null) {
            HiLogger.e("Interstitial ad is not loaded or cached.")
            return
        }
        HiLogger.d("show interstitial ad :${mTTFullScreenVideoAd}")
        mTTFullScreenVideoAd?.setFullScreenVideoAdInteractionListener(object :
            TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
            override fun onAdShow() {
                HiLogger.d("interstitial ad show")
                adCallback.onAdShow()
            }

            override fun onAdVideoBarClick() {
                HiLogger.d("interstitial ad click")
                adCallback.onAdClicked()
            }

            override fun onAdClose() {
                HiLogger.d("interstitial ad close")
                adCallback.onAdClosed()
            }

            override fun onVideoComplete() {
                HiLogger.d("interstitial ad complete")
                adCallback.onAdImpression()
            }

            override fun onSkippedVideo() {
                HiLogger.d("interstitial ad skip")
            }

        })
        this.mTTFullScreenVideoAd!!.showFullScreenVideoAd(currentActivity)

    }

    fun destroy() {
        if (mTTFullScreenVideoAd != null && mTTFullScreenVideoAd?.mediationManager != null) {
            mTTFullScreenVideoAd?.mediationManager!!.destroy()
        }
    }
}