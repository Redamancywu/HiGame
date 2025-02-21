//
//  HiGameSDKMax.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
import HiGameSDK_Core
import Foundation

public class HiGameSDKMax: HiGameSDKAdProtocol {
    // 模块名称
    public var moduleName: String = "HiGameSDKMax"
    
    // 初始化时传入的代理
    private var adDelegate: HiGameSDKInitDelegate?
    
    // 构造函数
    public init(delegate: HiGameSDKInitDelegate?) {
        self.adDelegate = delegate
        HiGameLog.d("HiGameSDKMax instance created.")
    
        
    }
    
    
    // 静态方法：自动注册模块
    public static func register() {
        HiGameSDKManager.shared.addModuleFactory { HiGameSDKMax(delegate: nil) }
        HiGameLog.d("HiGameSDKMax registered automatically.")
    }
    
    // 协议方法：初始化模块
    public func initialize(delegate: HiGameSDKInitDelegate?) {
        self.adDelegate = delegate
        HiGameLog.d("\(moduleName) initialized with delegate.")
    }
    
    // 协议方法：处理 SDK 初始化时的配置
    public func onInitSDK(_ config: HiGameSDKConfig) {
        guard let appid = config["appid"] as? String else {
            HiGameLog.e("Missing required configuration parameter: appid")
            return
        }
        HiGameLog.d("\(moduleName) received configuration with appid: \(appid)")
        let successful: NSObject = "Successful" as NSString
        adDelegate?.onInitSuccess(data: successful)
        
        // 示例：根据配置加载广告
        loadAd()
    }
    
    // 协议方法：加载广告
    public func loadAd() {
        HiGameLog.d("\(moduleName) is loading an ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async {
                HiGameLog.d("\(self.moduleName) ad loaded successfully.")
                self.adDelegate?.onInitSuccess(data: NSObject())
            }
        }
    }
    
    // 协议方法：展示广告
    public func showAd() {
        HiGameLog.d("\(moduleName) is showing an ad...")
        // 模拟广告展示逻辑
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            DispatchQueue.main.async {
                HiGameLog.d("\(self.moduleName) ad shown successfully.")
            }
        }
    }
}
