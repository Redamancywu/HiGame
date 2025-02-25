//
//  HiGameSDK.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
import Foundation

public class HiGameSDK {
    // 单例实例
    public static let shared = HiGameSDK()
    
    // 私有初始化方法，防止外部创建实例
    private init() {}
    
    /// 初始化 SDK
    /// - Parameter delegate: 初始化回调代理
    public func initSDK(_ delegate: HiGameSDKInitDelegate?) {
        // 调用核心管理类的初始化方法
        HiGameSDKManager.shared.initSDK(delegate: delegate)
    }
    
    /// 设置广告代理
    /// - Parameter delegate: 广告回调代理
    public func setAdDelegate(_ delegate: HiGameAdDelegate) {
        HiGameSDKManager.shared.setAdDelegate(delegate)
    }
    
    /// 加载广告
    /// - Parameter adType: 广告类型
    public func loadAd(_ adType: HiGameAdType) {
        HiGameSDKManager.shared.loadAd(adType)
    }
    
    /// 展示广告
    /// - Parameter adType: 广告类型
    public func showAd(_ adType: HiGameAdType) {
        HiGameSDKManager.shared.showAd(adType)
    }
    public func isAdReady(_ adType:HiGameAdType) ->[String : Bool] {
        HiGameSDKManager.shared.isAdReady(adType)
    }
    public func isAdLoad(_ adType:HiGameAdType) -> [String : Bool]  {
        HiGameSDKManager.shared.isAdLoaded(adType)
    }
}
