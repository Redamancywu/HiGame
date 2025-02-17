package com.neil.higamesdk.ad.banner

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.model.AdInfo
import com.higame.sdk.core.utils.ContextUtils
import com.higame.sdk.core.utils.UIUtils
import com.higame.sdk.core.utils.log.HiLogger
import com.neil.higamesdk.ad.R

class HiGameTTBannerAd private constructor() {

    private var bannerPosId: String? = null
    private var adNativeLoader: TTAdNative? = null
    private var adSlot: AdSlot? = null
    private lateinit var adCallback: HiGameAdCallback
    private var mBannerAd: TTNativeExpressAd? = null
    private lateinit var currentActivity: Activity
    private var mBannerView: View? = null
    private var mBannerContainer: ViewGroup? = null
    private var adInfo: AdInfo? = null

    companion object {
        @Volatile
        private var instance: HiGameTTBannerAd? = null

        fun getInstance(): HiGameTTBannerAd =
            instance ?: synchronized(this) {
                instance ?: HiGameTTBannerAd().also { instance = it }
            }
    }

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
     * 初始化广告配置
     */
    fun init(context: Context, config: HiGameConfig) {

        // 检查回调是否已设置
        if (!::adCallback.isInitialized) {
            throw IllegalStateException("Callback must be set before initializing ads.")
        }
        // 获取广告位 ID
        this.bannerPosId = config.features?.ads?.adUnitIds?.banner
        HiLogger.d("Banner position ID: $bannerPosId")
        if (bannerPosId.isNullOrEmpty()) {
            adCallback.onAdFailedToLoad(-3, "Banner position ID is null or empty")
            return
        }

        // 创建广告请求
        this.adSlot = AdSlot.Builder()
            .setAdCount(1)
            //.setAdId(bannerPosId!!)
            .setImageAcceptedSize(UIUtils.getScreenWidthInPx(context),UIUtils.dp2px(context,130))
            .setCodeId(bannerPosId)
            //.setImageAcceptedSize(300,130)
            .build()

        // 初始化广告加载器
        this.adNativeLoader = TTAdSdk.getAdManager().createAdNative(ContextUtils.getContext())
    }

    /**
     * 加载 Banner 广告
     */
    fun loadBannerAd() {
        adNativeLoader?.loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onError(code: Int, message: String?) {
                HiLogger.e( "Failed to load banner ad. Code: $code, Message: $message")
                adCallback.onAdFailedToLoad(code, message ?: "Unknown error")
            }

            override fun onNativeExpressAdLoad(data: MutableList<TTNativeExpressAd>?) {
                if (data.isNullOrEmpty()) {
                    adCallback.onAdFailedToLoad(-4, "No available ads")
                    return
                }

                // 取第一个广告对象
                val firstAd = data.firstOrNull()
                if (firstAd == null) {
                    adCallback.onAdFailedToLoad(-5, "First ad in the list is null")
                    return
                }

                // 存储广告数据到 adInfo
                mBannerAd = firstAd
                adInfo = AdInfo(
                    adId = bannerPosId!!, // 使用广告位 ID 作为 adId
                    adType = 1, // 假设 1 表示 Banner 类型
                    adName = "Banner Ad", // 固定名称
                    adDesc = "This is a banner ad loaded from TikTok SDK", // 描述
                    adStyleType = 2 // 假设 2 表示 Express 样式
                )

                // 回调广告加载成功
                adCallback.onAdLoaded(adInfo.toString())
            }
        })
    }
    private fun createBannerContainer(): ViewGroup {
        val container = FrameLayout(currentActivity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, // 宽度匹配父布局
                FrameLayout.LayoutParams.WRAP_CONTENT  // 高度包裹内容
            ).apply {
                gravity = Gravity.BOTTOM // 设置容器的位置为底部
            }
            visibility = View.GONE // 初始状态下隐藏容器
        }
        return container
    }

    /**
     * 显示 Banner 广告
     */
    fun showBannerAd() {
        if (mBannerAd == null) {
            adCallback.onAdFailedToShow("Banner ad is not loaded yet")
            return
        }

        if (mBannerContainer == null) {
            mBannerContainer = currentActivity.findViewById(R.id.ad_container)
            if (mBannerContainer == null) {
                // 如果 XML 中未定义广告容器，则动态创建一个
                mBannerContainer = createBannerContainer()
                val rootView = currentActivity.findViewById<ViewGroup>(android.R.id.content)
                rootView.addView(mBannerContainer)
            }
        }

        mBannerAd?.setExpressInteractionListener(object : TTNativeExpressAd.ExpressAdInteractionListener {
            override fun onAdClicked(view: View?, code: Int) {
                HiLogger.d("TTBannerAd onAdClicked :${view}, code: $code")
                adCallback.onAdClicked()
            }

            override fun onAdShow(view: View?, code: Int) {
                HiLogger.d("TTBannerAd onAdShow :${view}, code: $code")
                adCallback.onAdShow()
            }

            override fun onRenderFail(view: View?, message: String?, code: Int) {
                HiLogger.e("TTBannerAd onRenderFail :${view}, message: $message, code: $code")
                adCallback.onAdFailedToShow(message ?: "Render failed with unknown error")
            }

            override fun onRenderSuccess(view: View?, v: Float, v1: Float) {
                HiLogger.d("TTBannerAd onRenderSuccess :${view}, v: $v, v1: $v1")
                mBannerView = view
                if (view != null && mBannerContainer != null) {
                    mBannerContainer?.removeAllViews()
                    mBannerContainer?.addView(view)
                    mBannerContainer?.visibility = View.VISIBLE
                    adCallback.onAdShow()
                }
            }
        })

        mBannerAd?.setDislikeCallback(currentActivity, object : TTAdDislike.DislikeInteractionCallback {
            override fun onShow() {
                HiLogger.d("TTBannerAd onShow")
                adCallback.onAdShow()
            }

            override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                HiLogger.d("TTBannerAd onSelected")
            }

            override fun onCancel() {
                HiLogger.e("TTBannerAd onCancel")
                adCallback.onCancel()
            }
        })

        // 开始渲染广告
        mBannerAd?.render()
    }

    /**
     * 销毁 Banner 广告
     */
    fun destroyBannerAd() {
        mBannerAd?.destroy()
        mBannerView = null
        mBannerContainer?.let {
            it.removeAllViews()
            (it.parent as? ViewGroup)?.removeView(it) // 从父布局中移除容器
        }
        mBannerContainer = null
        adCallback.onAdClosed()
    }
}
