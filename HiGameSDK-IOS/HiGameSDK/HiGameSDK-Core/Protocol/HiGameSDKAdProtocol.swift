//
//  HiGameSDKAdProtocol.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

public protocol HiGameSDKAdProtocol : HiGameSdkBaseProtocol {
    func setAdDelegate(delegate: HiGameAdDelegate)
    func isAdReady(type: HiGameAdType) -> Bool
    func loadAd(type: HiGameAdType)
    func showAd(type: HiGameAdType)
    func closeAd(type: HiGameAdType)
    func autoLoaded(type: HiGameAdType)
    func isAdLoaded(type: HiGameAdType) -> Bool
}
