//
//  HiGameSDKMaxInterstitialAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import HiGameSDK_Core
import Foundation

class HiGameSDKMaxInterstitialAd {
    private var adDelegate: HiGameAdDelegate?
    private var isLoaded: Bool = false
    
    init() {}
    
    func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
    }
    
    func loadAd() {
        guard !isLoaded else {
            HiGameLog.d("Interstitial ad already loaded.")
            return
        }
        
        HiGameLog.d("Loading interstitial ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.isLoaded = true
                HiGameLog.d("Interstitial ad loaded successfully.")
                self.adDelegate?.adDidLoad(adType: .interstitial)
            }
        }
    }
    
    func showAd() {
        guard isLoaded else {
            HiGameLog.e("Interstitial ad not loaded yet.")
            adDelegate?.adDidFailToShow(adType: .interstitial, code: 401, message: "Ad not loaded")
            return
        }
        
        HiGameLog.d("Showing interstitial ad...")
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            HiGameLog.d("Interstitial ad shown successfully.")
            self.adDelegate?.adDidShow(adType: .interstitial)
            self.adDelegate?.adDidClick(adType: .interstitial)
        }
    }
    
    func closeAd() {
        isLoaded = false
        HiGameLog.d("Interstitial ad closed.")
        adDelegate?.adDidClose(adType: .interstitial)
    }
    
    func isAdLoaded() -> Bool {
        return isLoaded
    }
}