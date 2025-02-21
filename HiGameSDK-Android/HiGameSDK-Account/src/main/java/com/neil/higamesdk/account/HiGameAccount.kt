package com.neil.higamesdk.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.model.LoginParams
import com.higame.sdk.core.model.LoginType
import com.higame.sdk.core.model.UserInfo
import com.higame.sdk.core.module.IAccountModule
import com.higame.sdk.core.module.IModule
import com.higame.sdk.core.utils.log.HiLogger
import com.neil.higamesdk.account.login.GoogleLogin
import com.neil.higamesdk.account.login.HiLoginManager
import com.neil.higamesdk.account.login.TencentQQLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AutoService(IModule::class)
class HiGameAccount : IAccountModule{
    private  var currentActivity: FragmentActivity? = null
    private lateinit var moduleInitCallback:HiGameModuleInitCallback

    override fun init(context: Context, config: HiGameConfig) {
        HiLoginManager.getInstance().init(context)
        GoogleLogin.getInstance().init(context, config)
        TencentQQLogin.getInstance().init(context, config)
    }

    override fun setActivity(activity: Activity) {
        if (activity is FragmentActivity) {
            this.currentActivity = activity
            HiLogger.d("CurrentActivity set to: ${activity::class.simpleName}")
        } else {
            HiLogger.e("Invalid activity type, FragmentActivity required")
        }
    }

    override fun setCallback(callback: HiGameModuleInitCallback) {
        HiLogger.d("HiGameAccount setCallback")
        this.moduleInitCallback = callback
        HiLogger.d("HiGameAccount setCallback moduleInitCallback: $moduleInitCallback")
        GoogleLogin.getInstance().setCallback(moduleInitCallback)
        TencentQQLogin.getInstance().setInitCallback(moduleInitCallback)
    }

    override fun onPause() {
      
    }

    override fun onStop() {
      
    }

    override fun onDestroy() {
      
    }

    override fun login(screen: Boolean?, params: LoginParams?, callback: HiGameLoginCallback) {
         val activity = currentActivity ?: run {
            HiLogger.e("HiGameAccount loginWithScreen currentActivity is null")
            callback.onError(3002,"currentActivity is null")
             return
         }

        if (screen == true) {
            showLoginDialog(callback)
        } else {
            handleDirectLogin(params, activity, callback)
        }
    }
    /**
     * 显示登录对话框
     */
    private fun showLoginDialog(callback: HiGameLoginCallback) {
        val activity = currentActivity ?: run {
            HiLogger.e("HiGameAccount loginWithScreen currentActivity is null")
            return
        }

        val loginDialog = LoginDialogFragment.newInstance()
        loginDialog.setLoginInteractionListener(object : LoginDialogFragment.OnLoginInteractionListener {
            override fun onLoginClicked(username: String, password: String) {
                HiLogger.d("User selected account login with username: $username")
                handleAccountLogin(username, password, callback)
            }

            override fun onGuestLoginClicked() {
                HiLogger.d("User selected guest login")
                HiLoginManager.getInstance().loginAsGuest(callback)
            }

            override fun onSocialLoginClicked(provider: LoginDialogFragment.SocialProvider) {
                HiLogger.d("User selected social login with provider: $provider")
                when (provider) {
                    LoginDialogFragment.SocialProvider.GOOGLE -> handleGoogleLogin(activity, callback)
                    LoginDialogFragment.SocialProvider.QQ -> handleQQLogin(activity, callback)
                    else -> callback.onError(-1, "Unsupported social login provider", null)
                }
            }
        })
        loginDialog.show(activity.supportFragmentManager, "login_dialog")
    }

    /**
     * 处理直接登录逻辑
     */
    private fun handleDirectLogin(
        params: LoginParams?,
        activity: FragmentActivity,
        callback: HiGameLoginCallback
    ) {
        when (params) {
            is LoginParams.GuestLogin -> {
                HiLogger.d("Performing guest login")
                HiLoginManager.getInstance().loginAsGuest(callback)
            }
            is LoginParams.AccountLogin -> {
                HiLogger.d("Performing account login with username: ${params.username}")
                handleAccountLogin(params.username, params.password, callback)
            }
            is LoginParams.PlatformLogin -> {
                HiLogger.d("Performing platform login with type: ${params.platform}")
                handlePlatformLogin(params.platform, activity, callback)
            }
            null -> {
                HiLogger.e("Invalid login parameters: params cannot be null when screen is false")
                callback.onError(-1, "Invalid login parameters", null)
            }
        }
    }

    /**
     * 处理账号密码登录
     */
    private fun handleAccountLogin(username: String, password: String, callback: HiGameLoginCallback) {
        HiLogger.d("Starting account login with username: $username")
        // TODO: 实现账号密码登录逻辑
        callback.onError(-1, "Account login not implemented yet", null)
    }

    /**
     * 处理平台登录逻辑
     */
    private fun handlePlatformLogin(
        platform: LoginType,
        activity: FragmentActivity,
        callback: HiGameLoginCallback
    ) {
        when (platform) {
            LoginType.GOOGLE -> handleGoogleLogin(activity, callback)
            LoginType.QQ -> handleQQLogin(activity, callback)
            else -> callback.onError(-1, "Unsupported platform login type", null)
        }
    }

    /**
     * 处理 Google 登录逻辑
     */
    private fun handleGoogleLogin(activity: FragmentActivity, callback: HiGameLoginCallback) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                GoogleLogin.getInstance().signIn(activity) { result ->
                    result.onSuccess { signInResult ->
                        val userInfo = UserInfo(
                            userId = signInResult.userId!!,
                            token = signInResult.idToken,
                            nickname = signInResult.accountName,
                            avatar = null,
                            username = signInResult.familyName ?: "Unknown"
                        )
                        callback.onSuccess(userInfo.toString())
                    }.onFailure { exception ->
                        callback.onError(301, exception.message ?: "Google login failed")
                    }
                }
            } catch (e: Exception) {
                callback.onError(302, e.message ?: "An unexpected error occurred during Google login")
            }
        }
    }

    /**
     * 处理 QQ 登录逻辑
     */
    private fun handleQQLogin(activity: FragmentActivity, callback: HiGameLoginCallback) {
        TencentQQLogin.getInstance().login(activity) { success, errorMessage, userInfoMap ->
            if (success && userInfoMap != null) {
                val userInfo = UserInfo(
                    userId = userInfoMap["openid"] as? String ?: "",
                    token = userInfoMap["access_token"] as? String ?: "",
                    nickname = userInfoMap["nickname"] as? String ?: "Unknown",
                    avatar = userInfoMap["figureurl_qq_2"] as? String,
                    username = userInfoMap["nickname"] as? String ?: "Unknown"
                )
                HiLogger.i("QQ登录成功，用户信息：$userInfo")
                callback.onSuccess(userInfo.toString())
            } else {
                HiLogger.e("QQ登录失败：$errorMessage")
                callback.onError(401, errorMessage ?: "QQ登录失败")
            }
        }
    }

    override fun logout() {
        HiLogger.d("HiGameAccount logout")
    }

    override fun getUserInfo(): UserInfo? {
      return null
    }

    override fun isLoggedIn(): Boolean {
      return true
    }

    override fun bindAccount(accountType: String) {
      
    }

    override fun unbindAccount(accountType: String) {
      
    }

    override fun getBindAccounts(): List<String> {
      return emptyList()
    }

    override fun getModuleName(): String {
        return "HiGameAccount"
    }

    override fun onResume() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TencentQQLogin.getInstance().onActivityResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let {
            TencentQQLogin.getInstance().onNewIntent(it)
        }
    }

}