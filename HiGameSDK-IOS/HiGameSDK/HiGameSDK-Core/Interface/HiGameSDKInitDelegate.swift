//
//  HiGameSDKInitDelegate.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

public protocol HiGameSDKInitDelegate : AnyObject {
    /// 初始化成功的回调
    func onInitSuccess(data: NSObject)
    
    /// 初始化失败的回调
    func onInitFailed(code: Int, errorMessage: String)
}
