//
//  HiGameSDKLogin.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/24.
//
import HiGameSDK_Core

public class HiGameSDKLogin : HiGameSDKAccountProtocol {

    // 初始化时传入的代理
    private var initDelegate: HiGameSDKInitDelegate?
    private var loginDelegate: HiGameSDKLoginDelegate?
    public func setLogindelegate(logindelegate: HiGameSDKLoginDelegate?) {
        self.loginDelegate = logindelegate
    }
    
    public var moduleName: String = "HiGameSDKLogin"
    
    public func initialize(_ delegate: HiGameSDKInitDelegate?) {
        self.initDelegate = delegate
        HiGameLog.d("HiGameLogin initialized successful ")
        
        initDelegate?.onInitSuccess(data:"HiGameLogin initialized successful")
    }
    public required init() {}

    public func login(type: HiGameLoginType, username: String?, password: String?) {
        HiGameLog.d("HiGameSDKLogin login start")
    }
    public func logout() {
        HiGameLog.d("HiGameSDKLogin logout start")
    }
    public func onInitSDK(_ config: HiGameSDKConfig) {
        HiGameLog.d("HiGameSDKLogin onInitSDK start")
        
    }

    
}
