//
//  HiGameLog.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
import Foundation

public class HiGameLog {
    // 日志级别枚举
    public enum LogLevel: String {
        case debug = "DEBUG"
        case info = "INFO"
        case warning = "WARNING"
        case error = "ERROR"
    }
    
    // ANSI 转义序列：定义颜色和样式
    private enum LogStyle {
        static let reset = "\u{001B}[0m" // 重置样式
        static let bold = "\u{001B}[1m" // 加粗
        static let red = "\u{001B}[31m" // 红色
        static let yellow = "\u{001B}[33m" // 黄色
        static let green = "\u{001B}[32m" // 绿色
        static let blue = "\u{001B}[34m" // 蓝色
        static let cyan = "\u{001B}[36m" // 青色
    }
    
    // 是否启用 ANSI 样式（默认启用）
    private static var isAnsiEnabled: Bool = {
        // 检查环境变量，判断终端是否支持 ANSI 转义序列
        if let term = ProcessInfo.processInfo.environment["TERM"], term.lowercased().contains("color") {
            return true
        }
        return false
    }()
    
    // 私有初始化方法，防止外部创建实例
    private init() {}
    
    // 静态方法：DEBUG 日志
    public static func d(
        _ message: String,
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) {
        log(level: .debug, message: message, file: file, function: function, line: line)
    }
    
    // 静态方法：INFO 日志
    public static func i(
        _ message: String,
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) {
        log(level: .info, message: message, file: file, function: function, line: line)
    }
    
    // 静态方法：WARNING 日志
    public static func w(
        _ message: String,
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) {
        log(level: .warning, message: message, file: file, function: function, line: line)
    }
    
    // 静态方法：ERROR 日志
    public static func e(
        _ message: String,
        file: String = #file,
        function: String = #function,
        line: Int = #line
    ) {
        log(level: .error, message: message, file: file, function: function, line: line)
    }
    
    // 内部日志方法
    private static func log(
        level: LogLevel,
        message: String,
        file: String,
        function: String,
        line: Int
    ) {
        // 提取模块名和类名
        let fileName = (file as NSString).lastPathComponent
        let className = fileName.replacingOccurrences(of: ".swift", with: "")
        
        // 格式化日志输出
        let logMessage = "[\(level.rawValue)] [\(className)] [\(function):\(line)] \(message)"
        
        // 根据日志级别设置样式
        let styledLogMessage: String
        if isAnsiEnabled {
            switch level {
            case .debug:
                styledLogMessage = "\(LogStyle.cyan)\(logMessage)\(LogStyle.reset)"
            case .info:
                styledLogMessage = "\(LogStyle.green)\(logMessage)\(LogStyle.reset)"
            case .warning:
                styledLogMessage = "\(LogStyle.yellow)\(logMessage)\(LogStyle.reset)"
            case .error:
                styledLogMessage = "\(LogStyle.red)\(LogStyle.bold)\(logMessage)\(LogStyle.reset)"
            }
        } else {
            // 如果不支持 ANSI 转义序列，则使用纯文本
            styledLogMessage = logMessage
        }
        
        // 打印日志
        print(styledLogMessage)
    }
}
