package com.higame.sdk.core.model

sealed class LoginParams {
    data class AccountLogin(
        val username: String,
        val password: String
    ) : LoginParams()

    data class PlatformLogin(
        val platform: LoginType,
    ) : LoginParams()

    data object GuestLogin : LoginParams()
}
enum class LoginType {
    GOOGLE,
    QQ,
    WECHAT,
    FACEBOOK,
    TWITTER,
    LINE

}