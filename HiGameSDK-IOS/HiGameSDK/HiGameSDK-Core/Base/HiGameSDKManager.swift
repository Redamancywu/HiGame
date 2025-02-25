import Foundation

public class HiGameSDKManager {
    // 单例实例
    public static let shared = HiGameSDKManager()
    
    // 线程安全队列
    private let moduleQueue = DispatchQueue(label: "com.higame.sdk.moduleQueue", attributes: .concurrent)
    
    // 已注册的模块
    private var modules: [String: HiGameSdkBaseProtocol] = [:]
    
    // 模块初始化状态
    private var moduleStates: [String: ModuleState] = [:]
    
    // 引用 HiGameSDKConfig 的单例实例
    private var config: HiGameSDKConfig {
        return HiGameSDKConfig.shared
    }
    
    // 私有初始化方法
    private init() {}
    
    // 模块状态枚举
    private enum ModuleState {
        case uninitialized
        case initializing
        case initialized
        case failed(String)
    }
    
    // 初始化 SDK
    public func initSDK(delegate: HiGameSDKInitDelegate?) {
        moduleQueue.async {
            // 1. 检查配置是否加载成功
            guard !self.config.isEmpty else {
                HiGameLog.e("初始化失败：配置文件未加载或为空")
                delegate?.onInitFailed(code: 500, errorMessage: "Configuration file is missing or invalid.")
                return
            }
            
            // 2. 加载模块配置文件并注册模块
            self.loadAndRegisterModules()
            
            // 3. 初始化所有已注册模块
            self.initializeModules(delegate: delegate)
        }
    }
    
    // 加载模块配置文件并注册模块
    private func loadAndRegisterModules() {
        // 获取 Modules.plist 的路径
        guard let path = Bundle(for: HiGameSDKManager.self).path(forResource: "HiGameSDKModules", ofType: "plist"),
              let moduleNames = NSArray(contentsOfFile: path) as? [String] else {
            HiGameLog.e("无法加载模块配置文件 HiGameSDKModules.plist")
            return
        }
        
        HiGameLog.d("从 Modules.plist 加载模块列表：\(moduleNames)")
        
        // 遍历模块名称并动态加载模块
        for moduleName in moduleNames {
            if let moduleClass = NSClassFromString(moduleName) as? HiGameSdkBaseProtocol.Type {
                let module = moduleClass.init()
                self.modules[module.moduleName] = module
                self.moduleStates[module.moduleName] = .uninitialized
                HiGameLog.d("模块 \(moduleName) 注册成功")
            } else {
                HiGameLog.e("模块 \(moduleName) 未找到或未加载")
            }
        }
    }
    // 初始化所有已注册模块
    private func initializeModules(delegate: HiGameSDKInitDelegate?) {
        HiGameLog.d("开始初始化SDK，当前已注册模块数: \(self.modules.count)")
        
        guard !self.modules.isEmpty else {
            HiGameLog.e("初始化失败：没有注册任何模块")
            delegate?.onInitFailed(code: 500, errorMessage: "No modules registered.")
            return
        }
        
        var moduleResults: [String: Any] = [:] // 存储每个模块的初始化结果
        let group = DispatchGroup()
        
        for (moduleName, module) in self.modules {
            group.enter() // 进入组
            
            let internalDelegate = InternalDelegate(moduleName: moduleName) { success, error in
                if success {
                    moduleResults[moduleName] = ["status": "success"]
                } else if let error = error {
                    moduleResults[moduleName] = ["status": "failed", "error": error]
                }
                
                group.leave() // 离开组
            }
            
            module.initialize(internalDelegate)
            module.onInitSDK(self.config) // 使用 HiGameSDKConfig.shared
        }
        // 等待所有模块初始化完成
        group.notify(queue: DispatchQueue.global()) {
            // 构造返回结果
            let resultData: [String: Any] = [
                "totalModules": self.modules.count,
                "moduleResults": moduleResults
            ]
            
            // 转换为 JSON 字符串
            do {
                let jsonData = try JSONSerialization.data(withJSONObject: resultData, options: .prettyPrinted)
                let jsonString = String(data: jsonData, encoding: .utf8) ?? ""
                
                // 判断是否有失败模块
                let failedModules = moduleResults.filter { ($0.value as? [String: Any])?["status"] as? String == "failed" }
                if failedModules.isEmpty {
                    HiGameLog.d("initSDK success data: \(jsonString)")
                    delegate?.onInitSuccess(data: jsonString)
                } else {
                    let errorMessage = "部分模块初始化失败: \(failedModules)"
                    HiGameLog.e("initSDK failed message: \(errorMessage)")
                    delegate?.onInitFailed(code: 400, errorMessage: errorMessage)
                }
            } catch {
                HiGameLog.e("JSON 序列化失败: \(error.localizedDescription)")
                delegate?.onInitFailed(code: 500, errorMessage: "Failed to serialize initialization results.")
            }
        }
    }
//    private func initializeModules(delegate: HiGameSDKInitDelegate?) {
//        HiGameLog.d("开始初始化SDK，当前已注册模块数: \(self.modules.count)")
//        
//        guard !self.modules.isEmpty else {
//            HiGameLog.e("初始化失败：没有注册任何模块")
//            delegate?.onInitFailed(code: 500, errorMessage: "No modules registered.")
//            return
//        }
//        
//        var successCount = 0
//        var failedModules: [String] = []
//        let totalModules = self.modules.count
//        
//        for (moduleName, module) in self.modules {
//            let internalDelegate = InternalDelegate(moduleName: moduleName) { success, error in
//                if success {
//                    successCount += 1
//                } else if let error = error {
//                    failedModules.append(moduleName)
//                }
//                
//                if successCount + failedModules.count == totalModules {
//                    if failedModules.isEmpty {
//                        let data = moduleName// 模拟返回的数据
//                        HiGameLog.d("initSDK success data:\(data)")
//                        delegate?.onInitSuccess(data: data)
//                    } else {
//                        let errorMessage = "Modules failed: \(failedModules.joined(separator: ", "))"
//                        HiGameLog.e("initSDK failed message:\(errorMessage)")
//                        delegate?.onInitFailed(code: 400, errorMessage: errorMessage)
//                    }
//                }
//            }
//            
//            module.initialize(delegate)
//            module.onInitSDK(self.config) // 使用 HiGameSDKConfig.shared
//        }
//    }
    
    // 内部代理类
    private class InternalDelegate: HiGameSDKInitDelegate {
        private let moduleName: String
        private let completion: (Bool, String?) -> Void
        
        init(moduleName: String, completion: @escaping (Bool, String?) -> Void) {
            self.moduleName = moduleName
            self.completion = completion
        }
        
        func onInitSuccess(data: String) {
            HiGameLog.d("\(moduleName) 初始化成功")
            completion(true, nil)
        }
        
        func onInitFailed(code: Int, errorMessage: String) {
            HiGameLog.e("\(moduleName) 初始化失败，错误信息: \(errorMessage)")
            completion(false, errorMessage)
        }
    }
    
    // 获取指定模块实例
    public func getModule<T: HiGameSdkBaseProtocol>(_ type: T.Type) -> T? {
        for module in modules.values {
            if let targetModule = module as? T {
                return targetModule
            }
        }
        return nil
    }
    
    // 获取指定名称的模块实例
    public func getModuleByName(_ moduleName: String) -> HiGameSdkBaseProtocol? {
        return modules[moduleName]
    }
    
    // 获取所有已注册模块
    public func getAllModules() -> [HiGameSdkBaseProtocol] {
        return Array(modules.values)
    }
    
    // 检查模块是否已注册
    public func isModuleRegistered(_ moduleName: String) -> Bool {
        return modules[moduleName] != nil
    }
    
    // 获取模块状态
    public func getModuleState(_ moduleName: String) -> String {
        var state = ""
        moduleQueue.sync {
            switch moduleStates[moduleName] {
            case .uninitialized: state = "未初始化"
            case .initializing: state = "初始化中"
            case .initialized: state = "已初始化"
            case .failed(let error): state = "初始化失败: \(error)"
            case nil: state = "未注册"
            }
        }
        return state
    }
    // 设置广告代理
    public func setAdDelegate(_ delegate: HiGameAdDelegate) {
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    adModule.setAdDelegate(delegate: delegate)
                    HiGameLog.d("为模块 \(adModule.moduleName) 设置广告代理")
                }
            }
        }
    }
    
    // 加载广告
    public func loadAd(_ adType: HiGameAdType) {
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    adModule.loadAd(type: adType)
                    HiGameLog.d("为模块 \(adModule.moduleName) 加载广告类型 \(adType)")
                }
            }
        }
    }
    
    // 展示广告
    public func showAd(_ adType: HiGameAdType) {
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    adModule.showAd(type: adType)
                    HiGameLog.d("为模块 \(adModule.moduleName) 展示广告类型 \(adType)")
                }
            }
        }
    }
    public func isAdReady(_ adType: HiGameAdType) -> [String: Bool] {
        var results: [String: Bool] = [:]
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    let isReady = adModule.isAdReady(type: adType)
                    results[adModule.moduleName] = isReady
                    HiGameLog.d("为模块 \(adModule.moduleName) 是否准备好广告广告类型 \(adType): \(isReady)")
                }
            }
        }
        return results
    }

    public func isAdLoaded(_ adType: HiGameAdType) -> [String: Bool] {
        var results: [String: Bool] = [:]
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    let isLoaded = adModule.isAdLoaded(type: adType)
                    results[adModule.moduleName] = isLoaded
                    HiGameLog.d("为模块 \(adModule.moduleName) 准备加载广告类型 \(adType): \(isLoaded)")
                }
            }
        }
        return results
    }
    public func closeAd(_ adType:HiGameAdType) {
        moduleQueue.sync {
            for module in modules.values {
                if let adModule = module as? HiGameSDKAdProtocol {
                    adModule.closeAd(type: adType)
                    HiGameLog.d("为模块 \(adModule.moduleName) 关闭广告 \(adType)")
                }
            }
        }
    }

}
