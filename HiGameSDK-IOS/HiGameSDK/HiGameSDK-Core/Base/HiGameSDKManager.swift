//
//  HiGameSDKManager.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import Foundation

public class HiGameSDKManager {
    // 单例实例
    public static let shared = HiGameSDKManager()
    
    // 已注册的模块
    private var modules: [String: HiGameSdkBaseProtocol] = [:]
    
    // 模块工厂列表
    private var moduleFactories: [() -> HiGameSdkBaseProtocol] = []
    
    // 私有初始化方法，防止外部创建实例
    private init() {}
    
    // 添加模块工厂
    public func addModuleFactory(_ factory: @escaping () -> HiGameSdkBaseProtocol) {
        moduleFactories.append(factory)
    }
    
    // 自动注册模块
    private func autoRegisterModules() {
        for factory in moduleFactories {
            let module = factory()
            modules[module.moduleName] = module
            HiGameLog.d("\(module.moduleName) registered successfully.")
        }
    }
    
    // 初始化所有模块
    public func initSDK(Initdelegate: HiGameSDKInitDelegate?) {
        // 自动注册模块
        autoRegisterModules()
        
        guard !modules.isEmpty else {
            Initdelegate?.onInitFailed(code: 500, errorMessage: "No modules registered.")
            return
        }
        
        var successCount = 0
        var failedModules: [String] = []
        let totalModules = modules.count
        
        for (moduleName, module) in modules {
            // 创建 InternalDelegate 实例并传递回调
            let internalDelegate = InternalDelegate(moduleName: moduleName) { success, error in
                if success {
                    successCount += 1
                } else if let error = error {
                    failedModules.append(moduleName)
                }
                // 检查是否所有模块都已完成初始化
                if successCount + failedModules.count == totalModules {
                    if failedModules.isEmpty {
                        // 所有模块初始化成功
                        let data = NSObject() // 模拟返回的数据
                        HiGameLog.e("initSDK success data:\(data)")
                        Initdelegate?.onInitSuccess(data: data)
                    } else {
                        // 至少有一个模块初始化失败
                        let errorMessage = "Modules failed: \(failedModules.joined(separator: ", "))"
                        HiGameLog.e("initSDK failed message:\(errorMessage)")
                        Initdelegate?.onInitFailed(code: 400, errorMessage: errorMessage)
                    }
                }
            }
            // 调用模块的初始化方法
            module.initialize(delegate: internalDelegate as HiGameSDKInitDelegate)
        }
    }
    
    // 内部代理类，用于收集每个模块的初始化结果
    private class InternalDelegate: HiGameSDKInitDelegate {
        private let moduleName: String
        private let completion: (Bool, String?) -> Void
        
        init(moduleName: String, completion: @escaping (Bool, String?) -> Void) {
            self.moduleName = moduleName
            self.completion = completion
        }
        
        func onInitSuccess(data: NSObject) {
            HiGameLog.d("\(moduleName) initialized successfully.")
            completion(true, nil)
        }
        
        func onInitFailed(code: Int, errorMessage: String) {
            HiGameLog.d("\(moduleName) initialization failed with message: \(errorMessage)")
            completion(false, errorMessage)
        }
    }
}
