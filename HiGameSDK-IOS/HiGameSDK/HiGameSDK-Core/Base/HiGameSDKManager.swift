import Foundation
import ObjectiveC

public class HiGameSDKManager {
    // 单例实例，使用 static let 确保线程安全
    public static let shared = HiGameSDKManager()
    
    // 线程安全的队列，确保在单例初始化时就创建
    private let moduleQueue: DispatchQueue
    
    // 已注册的模块
    private var modules: [String: HiGameSdkBaseProtocol] = [:]
    
    // 模块工厂列表
    private var moduleFactories: [() -> HiGameSdkBaseProtocol] = []
    
    // 模块初始化状态
    private var moduleStates: [String: ModuleState] = [:]
    
    // 是否已完成模块的自动注册
    private var isModulesRegistered = false
    
    // 私有初始化方法，防止外部创建实例
    private init() {
        // 优先初始化 moduleQueue
        moduleQueue = DispatchQueue(label: "com.higame.sdk.moduleQueue", attributes: .concurrent)
        
        // 延迟初始化模块
        registerModulesIfNeeded()
    }
    
    // 延迟初始化模块
    private func registerModulesIfNeeded() {
        moduleQueue.async(flags: .barrier) {
            guard !self.isModulesRegistered else { return }
            
            // 获取所有遵循 HiGameSdkBaseProtocol 协议的类
            let moduleTypes = Bundle.main.allClasses.filter { type in
                return class_conformsToProtocol(type, HiGameSdkBaseProtocol.self)
            }
            
            HiGameLog.d("发现 \(moduleTypes.count) 个可用模块")
            
            // 注册每个模块
            for moduleType in moduleTypes {
                if let moduleClass = moduleType as? HiGameSdkBaseProtocol.Type {
                    let factory = { moduleClass.init() }
                    self.moduleFactories.append(factory)
                    HiGameLog.d("自动注册模块: \(moduleClass)")
                }
            }
            
            // 立即创建并注册所有模块实例
            for factory in self.moduleFactories {
                let module = factory()
                self.modules[module.moduleName] = module
                self.moduleStates[module.moduleName] = .uninitialized
                HiGameLog.d("\(module.moduleName) 注册成功")
            }
            
            self.isModulesRegistered = true
        }
    }
    
    // 模块状态枚举
    private enum ModuleState {
        case uninitialized
        case initializing
        case initialized
        case failed(String)
    }
    
    // 添加模块工厂
    public func addModuleFactory(_ factory: @escaping () -> HiGameSdkBaseProtocol) {
        moduleQueue.async(flags: .barrier) {
            self.moduleFactories.append(factory)
            HiGameLog.d("Module factory added. Current factory count: \(self.moduleFactories.count)")
        }
    }
    
    // 自动注册模块
    private func autoRegisterModules() {
        moduleQueue.async(flags: .barrier) {
            HiGameLog.d("开始自动注册模块，当前工厂数量: \(self.moduleFactories.count)")
            for factory in self.moduleFactories {
                let module = factory()
                HiGameLog.d("正在注册模块: \(module.moduleName)")
                self.modules[module.moduleName] = module
                self.moduleStates[module.moduleName] = .uninitialized
                HiGameLog.d("\(module.moduleName) 注册成功")
            }
            HiGameLog.d("模块注册完成，总注册模块数: \(self.modules.count)")
        }
    }
    
    // 初始化所有模块
    public func initSDK(Initdelegate: HiGameSDKInitDelegate?) {
        // 确保模块已注册
        registerModulesIfNeeded()
        
        moduleQueue.async {
            HiGameLog.d("开始初始化SDK，当前已注册模块数: \(self.modules.count)")
            
            guard !self.modules.isEmpty else {
                HiGameLog.e("初始化失败：没有注册任何模块")
                Initdelegate?.onInitFailed(code: 500, errorMessage: "No modules registered.")
                return
            }
            
            var successCount = 0
            var failedModules: [String] = []
            let totalModules = self.modules.count
            
            for (moduleName, module) in self.modules {
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
}
