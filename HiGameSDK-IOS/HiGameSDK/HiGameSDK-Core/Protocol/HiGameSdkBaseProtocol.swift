//
//  HiGameSdkBaseProtocol.swift
//  HiGameSDK
//
//  Created by neillwu on 2025/2/20.
//

public protocol HiGameSdkBaseProtocol {
    //模块名称
    var moduleName:String { get }
    //初始化
    init()
    func initialize(_ delegate: HiGameSDKInitDelegate?)
    
    func onInitSDK(_ config : HiGameSDKConfig)
    
}
