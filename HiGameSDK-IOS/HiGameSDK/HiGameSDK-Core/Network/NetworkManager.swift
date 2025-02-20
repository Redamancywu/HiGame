//
//  NetworkManager.swift
//  HiGameSDK-Core
//
//  Created by neillwu on 2025/2/20.
//

import Foundation

/// 网络管理器
/// 单例类，作为网络请求的统一入口，封装NetworkService的功能
public class NetworkManager {
    /// 共享实例
    public static let shared = NetworkManager()
    
    /// 网络服务实例
    private let networkService: NetworkRequestable
    
    /// 私有初始化方法
    /// 使用默认配置初始化NetworkService
    private init() {
        self.networkService = NetworkService()
    }
    
    /// 发送GET请求
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - parameters: 查询参数
    ///   - headers: 请求头
    /// - Returns: 解码后的响应数据
    public func get<T: Decodable>(_ url: String,
                                 parameters: [String: Any]? = nil,
                                 headers: [String: String]? = nil) async throws -> T {
        return try await networkService.request(url,
                                               method: .get,
                                               parameters: parameters,
                                               headers: headers)
    }
    
    /// 发送POST请求
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - parameters: 请求体参数
    ///   - headers: 请求头
    /// - Returns: 解码后的响应数据
    public func post<T: Decodable>(_ url: String,
                                  parameters: [String: Any]? = nil,
                                  headers: [String: String]? = nil) async throws -> T {
        return try await networkService.request(url,
                                               method: .post,
                                               parameters: parameters,
                                               headers: headers)
    }
    
    /// 发送PUT请求
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - parameters: 请求体参数
    ///   - headers: 请求头
    /// - Returns: 解码后的响应数据
    public func put<T: Decodable>(_ url: String,
                                 parameters: [String: Any]? = nil,
                                 headers: [String: String]? = nil) async throws -> T {
        return try await networkService.request(url,
                                               method: .put,
                                               parameters: parameters,
                                               headers: headers)
    }
    
    /// 发送DELETE请求
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - parameters: 请求体参数
    ///   - headers: 请求头
    /// - Returns: 解码后的响应数据
    public func delete<T: Decodable>(_ url: String,
                                    parameters: [String: Any]? = nil,
                                    headers: [String: String]? = nil) async throws -> T {
        return try await networkService.request(url,
                                               method: .delete,
                                               parameters: parameters,
                                               headers: headers)
    }
    
    /// 上传文件
    /// - Parameters:
    ///   - url: 上传的URL字符串
    ///   - data: 要上传的文件数据
    ///   - fileName: 文件名
    ///   - mimeType: 文件的MIME类型
    ///   - parameters: 附加的表单参数
    ///   - headers: 请求头
    /// - Returns: 服务器响应数据
    public func upload(_ url: String,
                      data: Data,
                      fileName: String,
                      mimeType: String,
                      parameters: [String: Any]? = nil,
                      headers: [String: String]? = nil) async throws -> Data {
        return try await networkService.upload(url,
                                              data: data,
                                              fileName: fileName,
                                              mimeType: mimeType,
                                              parameters: parameters,
                                              headers: headers)
    }
    
    /// 下载文件
    /// - Parameters:
    ///   - url: 下载的URL字符串
    ///   - destination: 文件保存的本地路径
    ///   - headers: 请求头
    /// - Returns: 下载文件的本地URL
    public func download(_ url: String,
                        to destination: URL,
                        headers: [String: String]? = nil) async throws -> URL {
        return try await networkService.download(url,
                                                headers: headers,
                                                destination: destination)
    }
}