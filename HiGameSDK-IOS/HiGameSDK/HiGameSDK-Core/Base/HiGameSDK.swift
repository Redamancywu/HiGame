//
//  HiGameSDK.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//
public class HiGameSDK {
    func initSDK(_ Initdelegate: HiGameSDKInitDelegate?) {
        HiGameSDKManager.shared.initSDK(Initdelegate:Initdelegate )
    }
}
