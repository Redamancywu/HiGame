import SwiftUI
import HiGameSDK_Core


import SwiftUI

class LogManager: ObservableObject {
    // 日志内容
    @Published var logs: String = ""
    
    // 添加日志
    func addLog(_ message: String) {
        logs += "\(message)\n"
    }
}
struct ContentView: View {
    // 绑定日志管理器
    @StateObject private var logManager = LogManager()
    
    var body: some View {
        VStack(spacing: 0) { // 使用 VStack 布局，设置 spacing 为 0 减少间距
            // 文本输入框（占屏幕高度的 1/4）
            ScrollView {
                Text(logManager.logs)
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                    .padding()
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(8)
            }
            .frame(height: UIScreen.main.bounds.height / 3) // 设置固定高度为屏幕高度的 1/4
            
            Spacer() // 用于分隔文本框和按钮
            
            // 按钮容器
            HStack(spacing: 20) { // 使用 HStack 布局，设置按钮间距为 20
                // 初始化按钮
                Button(action: {
                    initializeSDK()
                }) {
                    Text("Initialize SDK")
                        .font(.headline)
                        .padding()
                        .frame(maxWidth: .infinity) // 让按钮宽度填满可用空间
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                
                // 广告按钮
                Button(action: {
                    loadAd()
                }) {
                    Text("Load Ad")
                        .font(.headline)
                        .padding()
                        .frame(maxWidth: .infinity) // 让按钮宽度填满可用空间
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }
            .padding(.horizontal, 20) // 水平方向添加内边距
            .padding(.bottom, 20) // 底部添加内边距
        }
        .edgesIgnoringSafeArea(.top) // 忽略顶部安全区域，让文本框紧贴屏幕顶部
        .onAppear {
            // 模拟初始化时的日志输出
            log("[INFO] App started.")
        }
    }
    
    // 模拟初始化 SDK
    private func initializeSDK() {
        log("[INFO] Initializing SDK...")
        
        // 模拟异步初始化
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async {
                
               // self.log("[SUCCESS] SDK initialized successfully.")
            }
        }
    }
    
    // 模拟加载广告
    private func loadAd() {
        log("[INFO] Loading ad...")
        
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async {
                self.log("[SUCCESS] Ad loaded successfully.")
            }
        }
    }
    
    // 自定义日志函数
    private func log(_ message: String) {
        print(message) // 保留原始打印功能
        logManager.addLog(message)
    }
}

#Preview {
    ContentView()
}
