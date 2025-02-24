//
//  HiGameAdDelegate.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

public protocol HiGameAdDelegate: AnyObject {
    /// 广告加载成功
    func adDidLoad(adType: HiGameAdType)
    
    /// 广告加载失败
    func adDidFailToLoad(adType: HiGameAdType, code: Int, message: String)
    
    /// 广告展示成功
    func adDidShow(adType: HiGameAdType)
    
    /// 广告展示失败
    func adDidFailToShow(adType: HiGameAdType, code: Int, message: String)
    
    /// 广告被点击
    func adDidClick(adType: HiGameAdType)
    
    /// 广告关闭
    func adDidClose(adType: HiGameAdType)
    
    /// 广告奖励发放
    func adDidReward(adType: HiGameAdType)
}