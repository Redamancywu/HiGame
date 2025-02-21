//
//  HiGameSDKMax.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
import HiGameSDK_Core
import Foundation

// 模块工厂函数
public func createHiGameSDKMaxModule() -> HiGameSdkBaseProtocol {
    return HiGameSDKMax()
}

public class HiGameSDKMax: HiGameSDKAdProtocol {
    // 模块名称
    public var moduleName: String = "HiGameSDKMax"
    
    // 初始化时传入的代理
    private var initDelegate: HiGameSDKInitDelegate?
    private var adDelegate: HiGameAdDelegate?
    
    // 各类型广告实例
    private let bannerAd = HiGameSDKMaxBannerAd()
    private let interstitialAd = HiGameSDKMaxInterstitialAd()
    private let rewardedAd = HiGameSDKMaxRewardedAd()
    private let nativeAd = HiGameSDKMaxNativeAd()
    private let splashAd = HiGameSDKMaxSplashAd()
    
    // 构造函数
    public required init() {
        HiGameLog.d("\(moduleName) instance created.")
        // 在初始化时自动注册模块
        HiGameSDKManager.shared.addModuleFactory(createHiGameSDKMaxModule)
    }
    
    // 协议方法：初始化模块
    public func initialize(delegate: HiGameSDKInitDelegate?) {
        self.initDelegate = delegate
        HiGameLog.d("\(moduleName) initialized with delegate.")
        
        // 在初始化完成后立即通知成功
        let initData = NSObject()
        initDelegate?.onInitSuccess(data: initData)
    }
    
    // 协议方法：处理 SDK 初始化时的配置
    public func onInitSDK(_ config: HiGameSDKConfig) {
        guard let appid = config["appid"] as? String else {
            HiGameLog.e("\(moduleName): Missing required configuration parameter: appid")
            initDelegate?.onInitFailed(code: 400, errorMessage: "Missing required configuration parameter: appid")
            return
        }
        
        HiGameLog.d("\(moduleName) received configuration with appid: \(appid)")
    }
    
    // 设置广告代理
    public func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
        bannerAd.setAdDelegate(delegate)
        interstitialAd.setAdDelegate(delegate)
        rewardedAd.setAdDelegate(delegate)
        nativeAd.setAdDelegate(delegate)
        splashAd.setAdDelegate(delegate)
        HiGameLog.d("\(moduleName) ad delegate set.")
    }
    
    // 检查指定类型的广告是否准备就绪
    public func isAdReady(type: HiGameAdType) -> Bool {
        switch type {
        case .banner:
            return bannerAd.isAdLoaded()
        case .interstitial:
            return interstitialAd.isAdLoaded()
        case .rewardedVideo:
            return rewardedAd.isAdLoaded()
        case .native:
            return nativeAd.isAdLoaded()
        case .splash:
            return splashAd.isAdLoaded()
        }
    }
    
    // 自动预加载广告
    public func autoLoaded(type: HiGameAdType) {
        if !isAdLoaded(type: type) {
            loadAd(type: type)
        }
    }
    
    // 加载指定类型的广告
    public func loadAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.loadAd()
        case .interstitial:
            interstitialAd.loadAd()
        case .rewardedVideo:
            rewardedAd.loadAd()
        case .native:
            nativeAd.loadAd()
        case .splash:
            splashAd.loadAd()
        }
    }
    
    // 展示指定类型的广告
    public func showAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.showAd()
        case .interstitial:
            interstitialAd.showAd()
        case .rewardedVideo:
            rewardedAd.showAd()
        case .native:
            nativeAd.showAd()
        case .splash:
            splashAd.showAd()
        }
    }
    
    // 关闭指定类型的广告
    public func closeAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.closeAd()
        case .interstitial:
            interstitialAd.closeAd()
        case .rewardedVideo:
            rewardedAd.closeAd()
        case .native:
            nativeAd.closeAd()
        case .splash:
            splashAd.closeAd()
        }
    }
    
    // 检查指定类型的广告是否已加载
    public func isAdLoaded(type: HiGameAdType) -> Bool {
        return isAdReady(type: type)
    }
}
