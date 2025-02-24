//
//  HiGameSDKLoginDelegate.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/24.
//

public protocol HiGameSDKLoginDelegate : AnyObject {
    func onLoginSuccess(data:String?)
    func onLoginError(code: Int?,errorMessage:String)
    func onLoginCancel(code:Int?,errorMessgae:String?)
}
