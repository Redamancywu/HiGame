//
//  NetworkProtocol.swift
//  HiGameSDK-Core
//
//  Created by neillwu on 2025/2/20.
//

import Foundation

/// HTTP请求方法枚举
public enum HTTPMethod: String {
    /// GET请求
    case get = "GET"
    /// POST请求
    case post = "POST"
    /// PUT请求
    case put = "PUT"
    /// DELETE请求
    case delete = "DELETE"
}

/// 网络请求错误枚举
public enum NetworkError: Error {
    /// URL无效
    case invalidURL
    /// 没有数据返回
    case noData
    /// 数据解码错误
    case decodingError
    /// 服务器错误，包含HTTP状态码
    case serverError(Int)
    /// 未知错误
    case unknown(Error)
}

/// 网络请求协议
/// 定义了基本的网络请求操作，包括普通请求、文件上传和下载功能
public protocol NetworkRequestable {
    /// 发送网络请求并解码响应数据
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - method: HTTP请求方法
    ///   - parameters: 请求参数，可选
    ///   - headers: 请求头，可选
    /// - Returns: 解码后的响应数据
    func request<T: Decodable>(_ url: String,
                             method: HTTPMethod,
                             parameters: [String: Any]?,
                             headers: [String: String]?) async throws -> T
    
    /// 上传文件
    /// - Parameters:
    ///   - url: 上传的URL字符串
    ///   - data: 要上传的文件数据
    ///   - fileName: 文件名
    ///   - mimeType: 文件的MIME类型
    ///   - parameters: 附加参数，可选
    ///   - headers: 请求头，可选
    /// - Returns: 服务器响应数据
    func upload(_ url: String,
                data: Data,
                fileName: String,
                mimeType: String,
                parameters: [String: Any]?,
                headers: [String: String]?) async throws -> Data
    
    /// 下载文件
    /// - Parameters:
    ///   - url: 下载的URL字符串
    ///   - headers: 请求头，可选
    ///   - destination: 文件保存的本地路径
    /// - Returns: 下载文件的本地URL
    func download(_ url: String,
                  headers: [String: String]?,
                  destination: URL) async throws -> URL
}