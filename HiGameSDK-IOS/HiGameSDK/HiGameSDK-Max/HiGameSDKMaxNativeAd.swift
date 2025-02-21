//
//  HiGameSDKMaxNativeAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import HiGameSDK_Core
import Foundation

class HiGameSDKMaxNativeAd {
    private var adDelegate: HiGameAdDelegate?
    private var isLoaded: Bool = false
    
    init() {}
    
    func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
    }
    
    func loadAd() {
        guard !isLoaded else {
            HiGameLog.d("Native ad already loaded.")
            return
        }
        
        HiGameLog.d("Loading native ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.isLoaded = true
                HiGameLog.d("Native ad loaded successfully.")
                self.adDelegate?.adDidLoad(adType: .native)
            }
        }
    }
    
    func showAd() {
        guard isLoaded else {
            HiGameLog.e("Native ad not loaded yet.")
            adDelegate?.adDidFailToShow(adType: .native, code: 401, message: "Ad not loaded")
            return
        }
        
        HiGameLog.d("Showing native ad...")
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            HiGameLog.d("Native ad shown successfully.")
            self.adDelegate?.adDidShow(adType: .native)
            self.adDelegate?.adDidClick(adType: .native)
        }
    }
    
    func closeAd() {
        isLoaded = false
        HiGameLog.d("Native ad closed.")
        adDelegate?.adDidClose(adType: .native)
    }
    
    func isAdLoaded() -> Bool {
        return isLoaded
    }
}