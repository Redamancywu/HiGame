//
//  HiGameSDKTTAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/24.
//
import HiGameSDK_Core
import BUAdSDK
import BUAdTestMeasurement

public class HiGameSDKTTAd: HiGameSDKAdProtocol {
    // 模块名称
    public var moduleName: String = "HiGameSDKTTAd"
    
    // 初始化时传入的代理
    private var initDelegate: HiGameSDKInitDelegate?
    private var adDelegate: HiGameAdDelegate?
    
    // Banner 和插屏广告实例
    private let bannerAd = HiGameSDKTTBannerAd()
    private let interstitialAd = HiGameSDKTTInterstitialAd()
    
    // MARK: - 初始化器
    public required init() {}
    
    // 设置广告代理
    public func setAdDelegate(delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
        bannerAd.adDelegate = delegate
        interstitialAd.adDelegate = delegate
    }
    
    // 初始化模块
    public func initialize(_ delegate: HiGameSDKInitDelegate?) {
        HiGameLog.d("HiGameSDKTTAd initialized start")
        self.initDelegate = delegate
    }
    
    // 初始化 SDK 配置
    public func onInitSDK(_ config: HiGameSDKConfig) {
        HiGameLog.d("HiGameSDKTTAd initialization start")
        BUAdTestMeasurementConfiguration().debugMode = true
        let configuration = BUAdSDKConfiguration.configuration()
        configuration.themeStatus = NSNumber(integerLiteral: 0)
        
        guard let appid = config["appid"] as? String else {
            HiGameLog.e("\(moduleName): Missing required configuration parameter: appid")
            initDelegate?.onInitFailed(code: 400, errorMessage: "Missing required configuration parameter: appid")
            return
        }
        
        configuration.ageGroup = .adult
        configuration.appID = appid
        configuration.useMediation = true
        
        BUAdSDKManager.start(asyncCompletionHandler: { (success, error) in
            if success {
                DispatchQueue.main.async {
                    // SDK 初始化成功
                    HiGameLog.d("HiGameSDKTTAd initialized successfully")
                    
                    // 调用初始化成功的回调
                    self.initDelegate?.onInitSuccess(data: success.description)
                    // 设置配置并初始化广告
                    self.bannerAd.setupWithConfig(config)
                    self.interstitialAd.setupWithConfig(config)
                }
            } else {
                // SDK 初始化失败
                let errorMessage = error?.localizedDescription ?? "Unknown error"
                HiGameLog.e("HiGameSDKTTAd initialized failed: \(errorMessage)")
                self.initDelegate?.onInitFailed(code: 500, errorMessage: errorMessage)
            }
        })
    }
    
    // MARK: - 广告操作方法
    public func isAdReady(type: HiGameAdType) -> Bool {
        switch type {
        case .banner:
            return bannerAd.isAdReady
        case .interstitial:
            return interstitialAd.isAdReady
        default:
            return false
        }
    }
    
    public func isAdLoaded(type: HiGameAdType) -> Bool {
        switch type {
        case .banner:
            return bannerAd.isAdLoaded
        case .interstitial:
            return interstitialAd.isAdLoaded
        default:
            return false
        }
    }
    
    public func closeAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.close()
        case .interstitial:
            interstitialAd.close()
        default:
            break
        }
    }
    
    public func loadAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.load()
        case .interstitial:
            interstitialAd.load()
        default:
            break
        }
    }
    
    public func showAd(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.show()
        case .interstitial:
            interstitialAd.show()
        default:
            break
        }
    }
    
    public func autoLoaded(type: HiGameAdType) {
        switch type {
        case .banner:
            bannerAd.autoLoad()
        case .interstitial:
            interstitialAd.autoLoad()
        default:
            break
        }
    }
}
