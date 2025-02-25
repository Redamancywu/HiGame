//
//  HiGameSDKTTBannerAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/24.
//
import HiGameSDK_Core
import BUAdSDK
import Foundation
public extension CGSize {
    // 支持的广告尺寸
    static let supportedAdSizes: [CGSize] = [
        CGSize(width: 320, height: 150),
        CGSize(width: 320, height: 200),
        CGSize(width: 320, height: 250),
        CGSize(width: 300, height: 130),
        CGSize(width: 300, height: 45),
        CGSize(width: 300, height: 75),
        CGSize(width: 320, height: 50),
        CGSize(width: 345, height: 194)

    ]
    
    // 检查尺寸是否支持
    func isSupportedAdSize() -> Bool {
        return CGSize.supportedAdSizes.contains { $0.equalTo(self) }
    }
}

// 继承自 NSObject 以遵循 BUNativeExpressBannerViewDelegate 协议
public class HiGameSDKTTBannerAd: NSObject, BUNativeExpressBannerViewDelegate {
    // 广告代理
    public var adDelegate: HiGameAdDelegate?
    
    // 穿山甲 Banner 广告实例
    public var bannerView: BUNativeExpressBannerView?
    
    // 配置参数
    public var config: HiGameSDKConfig
    
    // 是否启用轮播功能
    public var isAutoRefreshEnabled: Bool = false
    
    // 轮播间隔时间（默认 30 秒）
    public var refreshInterval: TimeInterval = 30
    
    // 广告尺寸
    public var adSize: CGSize = .zero
    
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
        guard let slotID = config["bannerSlotID"] as? String else {
            HiGameLog.e("HiGameSDKTTBannerAd: Missing required configuration parameter: bannerSlotID")
            adDelegate?.adDidFailToLoad(adType: .banner, code: -1, message: "Missing required configuration parameter: bannerSlotID")
            return
        }
        
        // 解析广告尺寸
        var adSizeValue: CGSize?
        if let bannerSizeDict = config["bannerSize"] as? [String: CGFloat],
           let width = bannerSizeDict["width"],
           let height = bannerSizeDict["height"] {
            adSizeValue = CGSize(width: width, height: height)
        } else {
            HiGameLog.e("HiGameSDKTTBannerAd: Invalid or missing bannerSize parameter")
            adDelegate?.adDidFailToLoad(adType: .banner, code: -1, message: "Invalid or missing bannerSize parameter")
            return
        }
        
        // 验证广告尺寸是否支持
        guard let adSize = adSizeValue, adSize.isSupportedAdSize() else {
            HiGameLog.e("HiGameSDKTTBannerAd: Unsupported banner size: \(adSizeValue?.debugDescription ?? "nil")")
            adDelegate?.adDidFailToLoad(adType: .banner, code: -1, message: "Unsupported banner size")
            return
        }
        
        // 获取是否启用轮播功能
        if let enableAutoRefresh = config["enableAutoRefresh"] as? Bool {
            isAutoRefreshEnabled = enableAutoRefresh
        }
        
        // 获取轮播间隔时间
        if let interval = config["refreshInterval"] as? TimeInterval {
            refreshInterval = max(30, min(interval, 120)) // 确保在 30s～120s 范围内
        }
        
        // 获取当前活动的 rootViewController
        let rootViewController = self.getCurrentRootViewController() ?? UIViewController()
        let interval = isAutoRefreshEnabled ? Int(refreshInterval) : 0
        
        self.adSize = adSize
        bannerView = BUNativeExpressBannerView(
            slotID: slotID,
            rootViewController: rootViewController,
            adSize: adSize,
            interval: interval
        )
        bannerView?.delegate = self
        
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad initialized with slotID: \(slotID), size: \(adSize), autoRefresh: \(isAutoRefreshEnabled), interval: \(refreshInterval)")
        HiGameLog.d("HiGameSDKTTBannerAd: Banner view initialized with rootViewController: \(rootViewController)")
        HiGameLog.d("HiGameSDKTTBannerAd: Banner view delegate set successfully")
    }

    // 获取当前活动的 rootViewController
    private func getCurrentRootViewController() -> UIViewController? {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first else {
            return nil
        }
        return window.rootViewController
    }
    
    // MARK: - 广告操作方法
    public func load() {
        guard let bannerView = bannerView else {
            HiGameLog.e("HiGameSDKTTBannerAd: Banner view is not initialized")
            return
        }
        
        // 加载广告
        bannerView.loadAdData()
        HiGameLog.d("HiGameSDKTTBannerAd: Loading banner ad")
    }
    
    public func show() {
        guard let bannerView = bannerView else {
            HiGameLog.e("HiGameSDKTTBannerAd: Banner view is not initialized")
            return
        }
        
        // 确保广告已准备好展示
        if !isAdReady {
            HiGameLog.e("HiGameSDKTTBannerAd: Banner ad is not ready to show")
            return
        }
        
        // 获取当前活动的窗口和安全区域底部偏移
        let (bottomInset, rootViewController) = getCurrentWindowInfo()
        
        // 设置广告位置
        let screenWidth = UIScreen.main.bounds.width
        bannerView.frame = CGRect(
            x: (screenWidth - adSize.width) / 2.0,
            y: UIScreen.main.bounds.height - adSize.height - bottomInset,
            width: adSize.width,
            height: adSize.height
        )
        
        // 添加到视图中
        if let rootVC = rootViewController {
            rootVC.view.addSubview(bannerView)
            HiGameLog.d("HiGameSDKTTBannerAd: Showing banner ad")
        } else {
            HiGameLog.e("HiGameSDKTTBannerAd: Failed to find rootViewController")
        }
    }

    // 获取当前活动的窗口信息（安全区域底部偏移和 rootViewController）
    private func getCurrentWindowInfo() -> (bottomInset: CGFloat, rootViewController: UIViewController?) {
        if #available(iOS 13.0, *) {
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let window = windowScene.windows.first else {
                return (0, nil)
            }
            let bottomInset = window.safeAreaInsets.bottom
            return (bottomInset, window.rootViewController)
        } else {
            // iOS 12 或更低版本
            let bottomInset = UIApplication.shared.windows.first?.safeAreaInsets.bottom ?? 0
            let rootViewController = UIApplication.shared.windows.first?.rootViewController
            return (bottomInset, rootViewController)
        }
    }
    
    // 关闭广告
    public func close() {
        guard let bannerView = bannerView else {
            HiGameLog.e("HiGameSDKTTBannerAd: Banner view is not initialized")
            return
        }
        
        // 从父视图中移除广告
        bannerView.removeFromSuperview()
        isAdReady = false
        isAdLoaded = false
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad closed")
    }
    
    // 自动加载广告
    public func autoLoad() {
        load()
        HiGameLog.d("HiGameSDKTTBannerAd: Auto loading banner ad")
    }
}

// MARK: - BUNativeExpressBannerViewDelegate 实现
extension HiGameSDKTTBannerAd {
    // 广告加载成功
    public func nativeExpressBannerAdDidLoad(_ bannerAdView: BUNativeExpressBannerView) {
        isAdReady = true
        isAdLoaded = true
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad loaded successfully")
        adDelegate?.adDidLoad(adType: .banner)
    }
    
    // 广告加载失败
    public func nativeExpressBannerAd(_ bannerAdView: BUNativeExpressBannerView, didFailWithError error: Error?) {
        isAdReady = false
        isAdLoaded = false
        let errorMessage = error?.localizedDescription ?? "Unknown error"
        HiGameLog.e("HiGameSDKTTBannerAd: Banner ad failed to load with error: \(errorMessage)")
        adDelegate?.adDidFailToLoad(adType: .banner, code: -1, message: errorMessage)
    }
    
    // 广告展示
    public func nativeExpressBannerAdDidShow(_ bannerAdView: BUNativeExpressBannerView) {
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad shown")
        adDelegate?.adDidShow(adType: .banner)
    }
    
    // 广告点击
    public func nativeExpressBannerAdDidClick(_ bannerAdView: BUNativeExpressBannerView) {
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad clicked")
        adDelegate?.adDidClick(adType: .banner)
    }
    
    // 广告关闭
    public func nativeExpressBannerAdDidClose(_ bannerAdView: BUNativeExpressBannerView) {
        HiGameLog.d("HiGameSDKTTBannerAd: Banner ad closed by user")
        adDelegate?.adDidClose(adType: .banner)
    }
}
