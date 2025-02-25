//
//  HiGameSDKTTInterstitialAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/25.
//

import HiGameSDK_Core
import BUAdSDK

public class HiGameSDKTTInterstitialAd: NSObject, BUNativeExpressFullscreenVideoAdDelegate {
    // 广告代理
    public var adDelegate: HiGameAdDelegate?
    
    // 穿山甲插屏广告实例
    public var interstitialAd: BUNativeExpressFullscreenVideoAd?
    
    // 配置参数
    public var config: HiGameSDKConfig
    
    // 标志位：广告是否已准备好展示
    public var isAdReady: Bool = false
    
    // 标志位：广告是否已加载完成
    public var isAdLoaded: Bool = false
    
    // MARK: - 初始化器
    public required override init() {
        self.config = HiGameSDKConfig.shared
        super.init()
    }
    
    // 设置配置并初始化广告
    public func setupWithConfig(_ config: HiGameSDKConfig) {
        guard let slotID = config["interstitialSlotID"] as? String else {
            HiGameLog.e("HiGameSDKTTInterstitialAd: Missing required configuration parameter: interstitialSlotID")
            adDelegate?.adDidFailToLoad(adType: .interstitial, code: -1, message: "Missing required configuration parameter: interstitialSlotID")
            return
        }
        
        // 创建插屏广告对象
        let rootViewController = UIApplication.shared.windows.first?.rootViewController ?? UIViewController()
        interstitialAd = BUNativeExpressFullscreenVideoAd(slotID: slotID)
        interstitialAd?.delegate = self
        
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad initialized with slotID: \(slotID)")
    }
    
    // MARK: - 广告操作方法
    public func load() {
        guard let interstitialAd = interstitialAd else {
            HiGameLog.e("HiGameSDKTTInterstitialAd: Interstitial ad is not initialized")
            return
        }
        
        // 加载广告
        interstitialAd.loadData()
        HiGameLog.d("HiGameSDKTTInterstitialAd: Loading interstitial ad")
    }
    
    public func show() {
        guard let interstitialAd = interstitialAd else {
            HiGameLog.e("HiGameSDKTTInterstitialAd: Interstitial ad is not initialized")
            return
        }
        
        // 确保广告已准备好展示
        if !isAdReady {
            HiGameLog.e("HiGameSDKTTInterstitialAd: Interstitial ad is not ready to show")
            return
        }
        
        // 展示广告
        if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
            interstitialAd.show(fromRootViewController: rootViewController)
            HiGameLog.d("HiGameSDKTTInterstitialAd: Showing interstitial ad")
        }
    }
    
    public func close() {
        interstitialAd = nil
        isAdReady = false
        isAdLoaded = false
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad closed and reset")
    }
    
    public func autoLoad() {
        load()
        HiGameLog.d("HiGameSDKTTInterstitialAd: Auto loading interstitial ad")
    }
}

// MARK: - BUNativeExpressFullscreenVideoAdDelegate 实现
extension HiGameSDKTTInterstitialAd {
    // 广告加载成功
    public func nativeExpressFullscreenVideoAdDidLoad(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd) {
        isAdReady = true
        isAdLoaded = true
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad loaded successfully")
        adDelegate?.adDidLoad(adType: .interstitial)
    }
    
    // 广告加载失败
    public func nativeExpressFullscreenVideoAd(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd, didFailWithError error: Error?) {
        isAdReady = false
        isAdLoaded = false
        let errorMessage = error?.localizedDescription ?? "Unknown error"
        HiGameLog.e("HiGameSDKTTInterstitialAd: Interstitial ad failed to load with error: \(errorMessage)")
        adDelegate?.adDidFailToLoad(adType: .interstitial, code: -1, message: errorMessage)
    }
    
    // 广告视频下载完成
    public func nativeExpressFullscreenVideoAdDidDownLoadVideo(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd) {
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad video downloaded successfully")
        // 建议在此回调后展示广告
        isAdReady = true
    }
    
    // 广告展示
    public func nativeExpressFullscreenVideoAdDidShow(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd) {
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad shown")
        adDelegate?.adDidShow(adType: .interstitial)
    }
    
    // 广告点击
    public func nativeExpressFullscreenVideoAdDidClick(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd) {
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad clicked")
        adDelegate?.adDidClick(adType: .interstitial)
    }
    
    // 广告关闭
    public func nativeExpressFullscreenVideoAdDidClose(_ fullscreenVideoAd: BUNativeExpressFullscreenVideoAd) {
        HiGameLog.d("HiGameSDKTTInterstitialAd: Interstitial ad closed by user")
        adDelegate?.adDidClose(adType: .interstitial)
        
        // 重置广告对象
        close()
        
        // 重新加载广告
        autoLoad()
    }
}
