//
//  HiGameSDKMaxSplashAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import HiGameSDK_Core
import Foundation

class HiGameSDKMaxSplashAd {
    private var adDelegate: HiGameAdDelegate?
    private var isLoaded: Bool = false
    
    init() {}
    
    func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
    }
    
    func loadAd() {
        guard !isLoaded else {
            HiGameLog.d("Splash ad already loaded.")
            return
        }
        
        HiGameLog.d("Loading splash ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.isLoaded = true
                HiGameLog.d("Splash ad loaded successfully.")
                self.adDelegate?.adDidLoad(adType: .splash)
            }
        }
    }
    
    func showAd() {
        guard isLoaded else {
            HiGameLog.e("Splash ad not loaded yet.")
            adDelegate?.adDidFailToShow(adType: .splash, code: 401, message: "Ad not loaded")
            return
        }
        
        HiGameLog.d("Showing splash ad...")
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            HiGameLog.d("Splash ad shown successfully.")
            self.adDelegate?.adDidShow(adType: .splash)
            
            // 模拟开屏广告自动关闭
            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                self.closeAd()
            }
        }
    }
    
    func closeAd() {
        isLoaded = false
        HiGameLog.d("Splash ad closed.")
        adDelegate?.adDidClose(adType: .splash)
    }
    
    func isAdLoaded() -> Bool {
        return isLoaded
    }
}