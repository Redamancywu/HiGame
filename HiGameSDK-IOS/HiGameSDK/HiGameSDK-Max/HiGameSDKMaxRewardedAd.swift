//
//  HiGameSDKMaxRewardedAd.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

import HiGameSDK_Core
import Foundation

class HiGameSDKMaxRewardedAd {
    private var adDelegate: HiGameAdDelegate?
    private var isLoaded: Bool = false
    
    init() {}
    
    func setAdDelegate(_ delegate: HiGameAdDelegate) {
        self.adDelegate = delegate
    }
    
    func loadAd() {
        guard !isLoaded else {
            HiGameLog.d("Rewarded ad already loaded.")
            return
        }
        
        HiGameLog.d("Loading rewarded ad...")
        // 模拟异步加载广告
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else { return }
                self.isLoaded = true
                HiGameLog.d("Rewarded ad loaded successfully.")
                self.adDelegate?.adDidLoad(adType: .rewardedVideo)
            }
        }
    }
    
    func showAd() {
        guard isLoaded else {
            HiGameLog.e("Rewarded ad not loaded yet.")
            adDelegate?.adDidFailToShow(adType: .rewardedVideo, code: 401, message: "Ad not loaded")
            return
        }
        
        HiGameLog.d("Showing rewarded ad...")
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            HiGameLog.d("Rewarded ad shown successfully.")
            self.adDelegate?.adDidShow(adType: .rewardedVideo)
            
            // 模拟用户观看完成并获得奖励
            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                self.adDelegate?.adDidClick(adType: .rewardedVideo)
                self.adDelegate?.adDidReward(adType: .rewardedVideo)
            }
        }
    }
    
    func closeAd() {
        isLoaded = false
        HiGameLog.d("Rewarded ad closed.")
        adDelegate?.adDidClose(adType: .rewardedVideo)
    }
    
    func isAdLoaded() -> Bool {
        return isLoaded
    }
}