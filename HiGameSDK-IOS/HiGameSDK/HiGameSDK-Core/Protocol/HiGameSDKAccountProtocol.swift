//
//  HiGameSDKAccountProtocol.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/24.
//

public protocol HiGameSDKAccountProtocol : HiGameSdkBaseProtocol {
    func setLogindelegate(logindelegate:HiGameSDKLoginDelegate?)
    /// 登录方法
    func login(type: HiGameLoginType,  username: String?,  password: String?)
    
    /// 登出方法
    func logout()
}

