//
//  ViewController.swift
//  HiGameSDK-SwiftDemo
//
//  Created by neillwu on 2025/2/20.
//

import UIKit
import HiGameSDK_Core


class ViewController: UIViewController {
    
    // 文本输出框
    private lazy var outputTextView: UITextView = {
        let textView = UITextView()
        textView.translatesAutoresizingMaskIntoConstraints = false
        textView.isEditable = false
        textView.font = .systemFont(ofSize: 14)
        textView.layer.borderWidth = 1
        textView.layer.borderColor = UIColor.lightGray.cgColor
        textView.layer.cornerRadius = 5
        return textView
    }()
    
    // 按钮栈视图
    private lazy var buttonStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .vertical
        stackView.spacing = 10
        stackView.distribution = .fillEqually
        return stackView
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        view.backgroundColor = .white
        
        // 添加子视图
        view.addSubview(outputTextView)
        view.addSubview(buttonStackView)
        
        // 设置约束
        NSLayoutConstraint.activate([
            outputTextView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            outputTextView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            outputTextView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            outputTextView.heightAnchor.constraint(equalTo: view.heightAnchor, multiplier: 0.4),
            
            buttonStackView.topAnchor.constraint(equalTo: outputTextView.bottomAnchor, constant: 20),
            buttonStackView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            buttonStackView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            buttonStackView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20)
        ])
        
        // 添加按钮
        let buttonTitles = ["初始化SDK", "加载Banner广告", "展示Banner广告", "加载插屏广告", "展示插屏广告","广告加载状态"]
        buttonTitles.forEach { title in
            let button = UIButton(type: .system)
            button.setTitle(title, for: .normal)
            button.backgroundColor = .systemBlue
            button.setTitleColor(.white, for: .normal)
            button.layer.cornerRadius = 5
            button.addTarget(self, action: #selector(buttonTapped(_:)), for: .touchUpInside)
            buttonStackView.addArrangedSubview(button)
        }
    }
    
    @objc private func buttonTapped(_ sender: UIButton) {
        guard let title = sender.title(for: .normal) else { return }
        
        switch title {
        case "初始化SDK":
            initializeSDK()
        case "加载Banner广告":
            loadBannerAd()
        case "展示Banner广告":
            showBannerAd()
        case "加载插屏广告":
            loadInterstitialAd()
        case "展示插屏广告":
            showInterstitialAd()
        case "广告加载状态":
            loadAdStatus()
        default:
            break
        }
    }
    
    private func appendLog(_ message: String) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            let newText = "\(Date().formatted()): \(message)\n\(self.outputTextView.text ?? "")"
            self.outputTextView.text = newText
        }
    }
    
    // SDK功能实现
    private func initializeSDK() {
        appendLog("开始初始化SDK...")
        HiGameSDK.shared.setAdDelegate(self) // 设置广告代理
        HiGameSDK.shared.initSDK(self)
    }
    
    private func loadBannerAd() {
        appendLog("开始加载Banner广告...")
        HiGameSDK.shared.loadAd(.banner)

        
    }
    private func loadAdStatus(){
        //检查是否已加载广告
        let loaderResults = HiGameSDK.shared.isAdLoad(.banner)
        // 检查广告是否准备好
        let readyResults = HiGameSDK.shared.isAdReady(.banner)
        HiGameLog.d("是否已加载广告:\(loaderResults),是否已经准备哦好广告:\(readyResults)")
        //检查是否已加载广告
        let InloaderResults = HiGameSDK.shared.isAdLoad(.interstitial)
        // 检查广告是否准备好
        let InreadyResults = HiGameSDK.shared.isAdReady(.interstitial)
        HiGameLog.d("是否已加载广告:\(InloaderResults),是否已经准备哦好广告:\(InreadyResults)")
    }
    
    private func showBannerAd() {
        appendLog("开始展示Banner广告...")
        HiGameSDK.shared.showAd(.banner)
    }
    
    private func loadInterstitialAd() {
        appendLog("开始加载插屏广告...")
        HiGameSDK.shared.loadAd(.interstitial)

    }
    
    private func showInterstitialAd() {
        appendLog("开始展示插屏广告...")
        HiGameSDK.shared.showAd(.interstitial)
    }
}

// MARK: - HiGameSDKInitDelegate
extension ViewController: HiGameSDKInitDelegate {
    func onInitSuccess(data: String) {
        appendLog("SDK初始化成功：\(data)")
    }
    
    func onInitFailed(code: Int, errorMessage: String) {
        appendLog("SDK初始化失败：[\(code)] \(errorMessage)")
    }
}

// MARK: - HiGameAdDelegate
extension ViewController: HiGameAdDelegate {
    func adDidLoad(adType: HiGameAdType) {
        appendLog("\(adType.rawValue) 广告加载成功")
    }
    
    func adDidFailToLoad(adType: HiGameAdType, code: Int, message: String) {
        appendLog("\(adType.rawValue) 广告加载失败：[\(code)] \(message)")
    }
    
    func adDidShow(adType: HiGameAdType) {
        appendLog("\(adType.rawValue) 广告展示成功")
    }
    
    func adDidFailToShow(adType: HiGameAdType, code: Int, message: String) {
        appendLog("\(adType.rawValue) 广告展示失败：[\(code)] \(message)")
    }
    
    func adDidClick(adType: HiGameAdType) {
        appendLog("\(adType.rawValue) 广告被点击")
    }
    
    func adDidClose(adType: HiGameAdType) {
        appendLog("\(adType.rawValue) 广告关闭")
    }
    
    func adDidReward(adType: HiGameAdType) {
        appendLog("\(adType.rawValue) 广告奖励发放")
    }
}
