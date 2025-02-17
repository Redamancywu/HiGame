package com.higame.sdk.core.callback

interface HiGameAdCallback {
    /**
     * 广告加载成功
     */
    fun onAdLoaded(message: String?)

    /**
     * 广告加载失败
     */
    fun onAdFailedToLoad(code:Int?, message: String?)

    /**
     * 广告展示
     */
    fun onAdShow()
    /**
     * 广告播放取消
     */
    fun onCancel()
    /**
     * 广告关闭
     */
    fun onAdClosed()
    /**
     * 广告点击
     */
    fun onAdClicked()

    /**
     * 广告播放完成
     */
    fun onAdImpression()

    /**
     * 广告播放失败
     */
    fun onAdFailedToShow(message: String?)


}