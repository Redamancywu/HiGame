//
//  HiGameSDKConfig.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
import Foundation

public class HiGameSDKConfig {
    // 单例实例
    public static let shared = HiGameSDKConfig()
    
    // 配置参数存储
    private var config: [String: Any] = [:]
    
    // 默认配置文件名
    private let defaultFileName = "HiGameSDKConfig"
    private let defaultFileExtension = "json"
    
    // 私有初始化方法，防止外部创建实例
    private init() {
        // 自动加载默认配置文件
        do {
            try loadDefaultConfig()
        } catch {
            print("Failed to load default configuration: \(error.localizedDescription)")
        }
    }
    
    // 加载默认配置文件
    private func loadDefaultConfig() throws {
        // 获取默认文件路径
        guard let filePath = Bundle.main.path(forResource: defaultFileName, ofType: defaultFileExtension) else {
            throw NSError(domain: "HiGameSDKConfig", code: 404, userInfo: [NSLocalizedDescriptionKey: "Default configuration file not found in bundle."])
        }
        
        print("Loading configuration file from path: \(filePath)")
        
        // 加载 JSON 文件
        try loadFromJSON(filePath: filePath)
    }
    
    // 加载 JSON 文件
    public func loadFromJSON(filePath: String) throws {
        guard let jsonData = FileManager.default.contents(atPath: filePath) else {
            throw NSError(domain: "HiGameSDKConfig", code: 404, userInfo: [NSLocalizedDescriptionKey: "File not found at path: \(filePath)"])
        }
        
        do {
            let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: [])
            if let dictionary = jsonObject as? [String: Any] {
                self.config = dictionary
                print("Configuration loaded successfully from JSON file.")
            } else {
                throw NSError(domain: "HiGameSDKConfig", code: 500, userInfo: [NSLocalizedDescriptionKey: "Invalid JSON format."])
            }
        } catch {
            throw error
        }
    }
    
    // 获取配置参数
    public subscript(key: String) -> Any? {
        return config[key]
    }
    
    // 检查配置是否为空
    public var isEmpty: Bool {
        return config.isEmpty
    }
    
    // 将当前配置导出为 JSON 字符串
    public func toJSONString(prettyPrinted: Bool = false) -> String? {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: config, options: prettyPrinted ? .prettyPrinted : [])
            return String(data: jsonData, encoding: .utf8)
        } catch {
            print("Failed to convert configuration to JSON string: \(error.localizedDescription)")
            return nil
        }
    }
}
