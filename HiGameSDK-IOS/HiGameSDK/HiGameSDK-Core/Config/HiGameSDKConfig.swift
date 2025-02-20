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
    
    // 私有初始化方法，防止外部创建实例
    private init() {}
    
    // 加载 JSON 文件
    public func loadFromJSON(filePath: String) throws {
        guard let jsonData = FileManager.default.contents(atPath: filePath) else {
            throw NSError(domain: "HiGameSDKConfig", code: 404, userInfo: [NSLocalizedDescriptionKey: "File not found at path: \(filePath)"])
        }
        
        do {
            let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: [])
            if let dictionary = jsonObject as? [String: Any] {
                self.config = dictionary
                HiGameLog.d("Configuration loaded successfully from JSON file.")
            } else {
                throw NSError(domain: "HiGameSDKConfig", code: 500, userInfo: [NSLocalizedDescriptionKey: "Invalid JSON format."])
            }
        } catch {
            throw error
        }
    }
    
    // 加载 TXT 文件（键值对格式）
    public func loadFromTXT(filePath: String) throws {
        guard let content = try? String(contentsOfFile: filePath, encoding: .utf8) else {
            throw NSError(domain: "HiGameSDKConfig", code: 404, userInfo: [NSLocalizedDescriptionKey: "File not found at path: \(filePath)"])
        }
        
        var parsedConfig: [String: Any] = [:]
        content.enumerateLines { line, _ in
            let components = line.split(separator: "=", maxSplits: 1, omittingEmptySubsequences: true)
            if components.count == 2 {
                let key = String(components[0]).trimmingCharacters(in: .whitespacesAndNewlines)
                let value = String(components[1]).trimmingCharacters(in: .whitespacesAndNewlines)
                parsedConfig[key] = value
            }
        }
        
        self.config = parsedConfig
        HiGameLog.d("Configuration loaded successfully from TXT file.")
    }
    
    // 获取配置参数
    public subscript(key: String) -> Any? {
        return config[key]
    }
    
    // 提供静态属性访问
    public static var appid: String? {
        return shared["appid"] as? String
    }
    
    public static var apiKey: String? {
        return shared["apiKey"] as? String
    }
    
    public static var environment: String? {
        return shared["environment"] as? String
    }
}
