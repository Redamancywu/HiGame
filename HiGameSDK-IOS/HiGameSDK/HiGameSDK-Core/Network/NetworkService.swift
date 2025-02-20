//
//  NetworkService.swift
//  HiGameSDK-Core
//
//  Created by neillwu on 2025/2/20.
//

import Foundation

public class NetworkService: NetworkRequestable {
    private let session: URLSession
    private let decoder: JSONDecoder
    
    public init(configuration: URLSessionConfiguration = .default) {
        self.session = URLSession(configuration: configuration)
        self.decoder = JSONDecoder()
    }
    
    /// 发送网络请求并解码响应数据
    /// - Parameters:
    ///   - url: 请求的URL字符串
    ///   - method: HTTP请求方法（GET、POST、PUT、DELETE）
    ///   - parameters: 请求参数，对于GET请求会转换为查询参数，其他请求方法会转换为JSON数据
    ///   - headers: 自定义请求头
    /// - Returns: 解码后的响应数据
    /// - Throws: NetworkError类型的错误
    public func request<T: Decodable>(_ url: String,
                                     method: HTTPMethod,
                                     parameters: [String: Any]?,
                                     headers: [String: String]?) async throws -> T {
        // 验证并创建URL对象
        guard let url = URL(string: url) else {
            throw NetworkError.invalidURL
        }
        
        // 创建URLRequest对象
        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        
        // 设置请求头
        headers?.forEach { request.setValue($1, forHTTPHeaderField: $0) }
        
        // 处理请求参数
        if let parameters = parameters {
            if method == .get {
                // GET请求：将参数转换为URL查询参数
                var components = URLComponents(url: url, resolvingAgainstBaseURL: false)!
                components.queryItems = parameters.map { URLQueryItem(name: $0.key, value: "\($0.value)") }
                request.url = components.url
            } else {
                // 其他请求：将参数转换为JSON数据
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                request.httpBody = try? JSONSerialization.data(withJSONObject: parameters)
            }
        }
        
        // 发送请求并获取响应
        let (data, response) = try await session.data(for: request)
        
        // 验证响应类型
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.unknown(NSError(domain: "", code: -1))
        }
        
        // 检查HTTP状态码
        guard (200...299).contains(httpResponse.statusCode) else {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        // 解码响应数据
        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw NetworkError.decodingError
        }
    }
    
    public func upload(_ url: String,
                      data: Data,
                      fileName: String,
                      mimeType: String,
                      parameters: [String: Any]?,
                      headers: [String: String]?) async throws -> Data {
        guard let url = URL(string: url) else {
            throw NetworkError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = HTTPMethod.post.rawValue
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        headers?.forEach { request.setValue($1, forHTTPHeaderField: $0) }
        
        var bodyData = Data()
        
        // 添加参数
        if let parameters = parameters {
            for (key, value) in parameters {
                bodyData.append("--\(boundary)\r\n".data(using: .utf8)!)
                bodyData.append("Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n".data(using: .utf8)!)
                bodyData.append("\(value)\r\n".data(using: .utf8)!)
            }
        }
        
        // 添加文件数据
        bodyData.append("--\(boundary)\r\n".data(using: .utf8)!)
        bodyData.append("Content-Disposition: form-data; name=\"file\"; filename=\"\(fileName)\"\r\n".data(using: .utf8)!)
        bodyData.append("Content-Type: \(mimeType)\r\n\r\n".data(using: .utf8)!)
        bodyData.append(data)
        bodyData.append("\r\n".data(using: .utf8)!)
        
        bodyData.append("--\(boundary)--\r\n".data(using: .utf8)!)
        request.httpBody = bodyData
        
        let (responseData, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.unknown(NSError(domain: "", code: -1))
        }
        
        guard (200...299).contains(httpResponse.statusCode) else {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        return responseData
    }
    
    public func download(_ url: String,
                        headers: [String: String]?,
                        destination: URL) async throws -> URL {
        guard let url = URL(string: url) else {
            throw NetworkError.invalidURL
        }
        
        var request = URLRequest(url: url)
        headers?.forEach { request.setValue($1, forHTTPHeaderField: $0) }
        
        let (tempURL, response) = try await session.download(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NetworkError.unknown(NSError(domain: "", code: -1))
        }
        
        guard (200...299).contains(httpResponse.statusCode) else {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        if FileManager.default.fileExists(atPath: destination.path) {
            try FileManager.default.removeItem(at: destination)
        }
        
        try FileManager.default.moveItem(at: tempURL, to: destination)
        
        return destination
    }
}