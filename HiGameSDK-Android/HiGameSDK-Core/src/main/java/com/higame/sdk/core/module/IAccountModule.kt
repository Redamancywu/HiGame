package com.higame.sdk.core.module

import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.model.LoginParams
import com.higame.sdk.core.model.UserInfo

interface IAccountModule : IModule {

    /**
     * 使用指定的登录参数进行登录
     * @param params 登录参数，可以是账号密码登录、平台登录或游客登录
     */
    fun login(screen : Boolean? ,params: LoginParams?,callback: HiGameLoginCallback)

    /**
     * 退出登录
     */
    fun logout()

    /**
     * 获取当前登录用户信息
     * @return 用户信息，未登录时返回null
     */
    fun getUserInfo(): UserInfo?

    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    fun isLoggedIn(): Boolean

    /**
     * 绑定第三方账号
     * @param accountType 要绑定的账号类型
     */
    fun bindAccount(accountType: String)

    /**
     * 解绑第三方账号
     * @param accountType 要解绑的账号类型
     */
    fun unbindAccount(accountType: String)

    /**
     * 获取已绑定的第三方账号列表
     * @return 已绑定的账号类型列表
     */
    fun getBindAccounts(): List<String>
}