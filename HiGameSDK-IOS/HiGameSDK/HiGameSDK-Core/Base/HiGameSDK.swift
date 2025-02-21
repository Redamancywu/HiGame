//
//  HiGameSDK.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
public class HiGameSDK {
    // 单例实例
    public static let shared = HiGameSDK()
    
    public func initSDK(_ Initdelegate: HiGameSDKInitDelegate?) {
        // 初始化SDK
        HiGameSDKManager.shared.initSDK(Initdelegate:Initdelegate)
    }
}
