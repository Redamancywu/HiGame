//
//  HiGameSDKMaxBannerAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import HiGameSDK_Core
import Foundation

class HiGameSDKMaxBannerAd {
    private var adDelegate: HiGameAdDelegate?
    private var isLoaded: Bool = false
    
    init() {}
    
    func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
    }
    
    func loadAd() {
        guard !isLoaded else {
            HiGameLog.d("Banner ad already loaded.")
            return
        }
        
        HiGameLog.d("Loading banner ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.isLoaded = true
                HiGameLog.d("Banner ad loaded successfully.")
                self.adDelegate?.adDidLoad(adType: .banner)
            }
        }
    }
    
    func showAd() {
        guard isLoaded else {
            HiGameLog.e("Banner ad not loaded yet.")
            adDelegate?.adDidFailToShow(adType: .banner, code: 401, message: "Ad not loaded")
            return
        }
        
        HiGameLog.d("Showing banner ad...")
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            HiGameLog.d("Banner ad shown successfully.")
            self.adDelegate?.adDidShow(adType: .banner)
        }
    }
    
    func closeAd() {
        isLoaded = false
        HiGameLog.d("Banner ad closed.")
        adDelegate?.adDidClose(adType: .banner)
    }
    
    func isAdLoaded() -> Bool {
        return isLoaded
    }
}