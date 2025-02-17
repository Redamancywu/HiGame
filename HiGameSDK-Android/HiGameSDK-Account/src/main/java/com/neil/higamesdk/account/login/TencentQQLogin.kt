package com.neil.higamesdk.account.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.higame.sdk.core.callback.HiGameInitCallback
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.utils.ContextUtils
import com.higame.sdk.core.utils.log.HiLogger
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

class TencentQQLogin private constructor() {

    private var mTencent: Tencent? = null
    private var mContext: Context? = null
    private var mAppId: String = ""
    private var mLoginListener: LoginListener? = null
    private lateinit var initCallback:HiGameModuleInitCallback

    companion object {
        @Volatile
        private var instance: TencentQQLogin? = null

        fun getInstance(): TencentQQLogin =
            instance ?: synchronized(this) {
                instance ?: TencentQQLogin().also { instance = it }
            }
    }
    fun setInitCallback(callback: HiGameModuleInitCallback) {
        initCallback = callback
        HiLogger.d("TencentQQLogin setInitCallback")
    }
    /**
     * 初始化 QQ 登录模块
     */
    fun init(context: Context, config: HiGameConfig) {
        mContext = ContextUtils.getContext()
        Tencent.setIsPermissionGranted(true)
        mAppId = config.features?.account?.qqAppId.toString()
        HiLogger.d("TencentQQLogin init initCallback: $initCallback")

        if (mAppId.isEmpty()) {
            val errorMessage = "QQ App ID is not configured"
            HiLogger.e(errorMessage)
            initCallback.let {
                HiLogger.d("Calling onFailed with message: $errorMessage")
                it.onFailed(400, errorMessage)
            } ?: HiLogger.e("initCallback is null in TencentQQLogin")
            return
        }

        try {
            mTencent = Tencent.createInstance(mAppId, context)
            HiLogger.i("QQ登录初始化完成，AppId: $mAppId")
        } catch (e: Exception) {
            HiLogger.e("Failed to create Tencent instance", e)
            initCallback.onFailed(500, "Failed to create Tencent instance: ${e.message}")
            return
        }

        // 调用成功回调，并传递成功信息
        val successMessage = "QQ登录模块初始化成功"
        val successData = mapOf(
            "appId" to mAppId,
            "status" to "initialized"
        )
        HiLogger.d("QQ登录模块初始化成功 Calling onSuccess with data: $successData")
        initCallback.let {
            HiLogger.d("Calling onSuccess with data: $successData")
            it.onSuccess(successMessage, successData)
        } ?: HiLogger.e("initCallback is null in TencentQQLogin")

        // 处理可能的 Intent 数据
        val intent = (context as? Activity)?.intent
        if (intent != null) {
            handleIntent(intent)
        } else {
            HiLogger.w("No valid Intent found during QQ login initialization")
        }
    }
    /**
     * 启动 QQ 登录流程
     */
    fun login(activity: Activity, callback: (Boolean, String?, Map<String, Any>?) -> Unit) {
        HiLogger.i("开始QQ登录流程")
        val listener = LoginListener(callback)
        mTencent?.login(activity, "scope", listener) ?: run {
            HiLogger.e("Tencent SDK未初始化")
            callback(false, "Tencent SDK未初始化", null)
        }
    }

    /**
     * 退出 QQ 登录
     */
    fun logout() {
        HiLogger.i("QQ登出")
        mTencent?.logout(mContext)
    }

    /**
     * 处理 onActivityResult 回调
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mTencent?.let {
            Tencent.onActivityResultData(requestCode, resultCode, data, mLoginListener)
        } ?: HiLogger.e("Tencent SDK未初始化，无法处理 onActivityResult")
    }

    /**
     * 检查是否已登录
     */
    fun isLogin(): Boolean {
        return mTencent?.isSessionValid ?: false
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(callback: (Boolean, String?, Map<String, Any>?) -> Unit) {
        if (!isLogin()) {
            HiLogger.e("获取用户信息失败：用户未登录")
            callback(false, "用户未登录", null)
            return
        }
        HiLogger.i("开始获取QQ用户信息")
        val listener = UserInfoListener(callback)
        mTencent?.let {
            it.openId?.let { openId ->
                it.accessToken?.let { token ->
                    com.tencent.connect.UserInfo(mContext, it.qqToken).getUserInfo(listener)
                } ?: run {
                    HiLogger.e("Access Token无效")
                    callback(false, "Access Token无效", null)
                }
            } ?: run {
                HiLogger.e("OpenID无效")
                callback(false, "OpenID无效", null)
            }
        } ?: run {
            HiLogger.e("Tencent SDK未初始化")
            callback(false, "Tencent SDK未初始化", null)
        }
    }

    /**
     * 处理新的 Intent 数据
     */
    fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    /**
     * 处理 Intent 数据
     */
     fun handleIntent(intent: Intent) {
        val uriData = intent.data
        if (uriData == null) {
            HiLogger.w("Intent中没有有效的URI数据")
            return
        }
        when (uriData.scheme) {
            "tencent$mAppId" -> handleTencentScheme(uriData)
            else -> handleUnknownScheme(uriData)
        }
    }

    /**
     * 处理腾讯 Scheme 的逻辑
     */
    private fun handleTencentScheme(uri: Uri) {
        HiLogger.d("处理腾讯Scheme: ${uri.toString()}")
        val action = uri.getQueryParameter("action")
        val result = uri.getQueryParameter("result")
        HiLogger.d("Action: $action, Result: $result")
    }

    /**
     * 处理未知 Scheme 的逻辑
     */
    private fun handleUnknownScheme(uri: Uri) {
        HiLogger.e("未知Scheme: ${uri.toString()}")
    }

    /**
     * 获取用户信息（无参数版本）
     *
     * @return 用户信息 Map 或 null（如果未登录或获取失败）
     */
    fun getUserInfo(): Map<String, Any>? {
        if (!isLogin()) {
            HiLogger.e("获取用户信息失败：用户未登录")
            return null
        }
        HiLogger.i("开始获取QQ用户信息（无参数版本）")
        val userInfo = mutableMapOf<String, Any>()
        val listener = object : IUiListener {
            override fun onComplete(response: Any?) {
                HiLogger.d(
                    "获取用户信息成功：$response"
                )
                if (response is JSONObject) {
                    try {
                        userInfo["nickname"] = response.optString("nickname")
                        userInfo["figureurl"] = response.optString("figureurl")
                        userInfo["figureurl_1"] = response.optString("figureurl_1")
                        userInfo["figureurl_2"] = response.optString("figureurl_2")
                        userInfo["figureurl_qq_1"] = response.optString("figureurl_qq_1")
                        userInfo["figureurl_qq_2"] = response.optString("figureurl_qq_2")
                        userInfo["gender"] = response.optString("gender")
                        userInfo["is_yellow_vip"] = response.optString("is_yellow_vip")
                        userInfo["vip"] = response.optString("vip")
                        userInfo["yellow_vip_level"] = response.optString("yellow_vip_level")
                        userInfo["level"] = response.optString("level")
                        userInfo["is_yellow_year_vip"] = response.optString("is_yellow_year_vip")

                        HiLogger.i("成功获取QQ用户信息：${response.optString("nickname")}")
                    } catch (e: Exception) {
                        HiLogger.e("解析用户信息失败: ${e.message}")
                        userInfo.clear()
                    }
                } else {
                    HiLogger.e("获取用户信息返回数据格式错误")
                    userInfo.clear()
                }
            }

            override fun onError(error: UiError?) {
                HiLogger.e("获取用户信息失败: ${error?.errorMessage}")
                userInfo.clear()
            }

            override fun onCancel() {
                HiLogger.e("获取用户信息已取消")
                userInfo.clear()
            }

            override fun onWarning(code: Int) {
                HiLogger.w("收到警告信息，Code: $code")
            }
        }

        mTencent?.let {
            it.openId?.let { openId ->
                it.accessToken?.let { token ->
                    com.tencent.connect.UserInfo(mContext, it.qqToken).getUserInfo(listener)
                } ?: run {
                    HiLogger.e("Access Token无效")
                    userInfo.clear()
                }
            } ?: run {
                HiLogger.e("OpenID无效")
                userInfo.clear()
            }
        } ?: run {
            HiLogger.e("Tencent SDK未初始化")
            userInfo.clear()
        }

        // 如果需要同步返回结果，可以通过阻塞或其他方式等待完成
        // 这里假设直接返回当前已填充的 userInfo
        return if (userInfo.isEmpty()) null else userInfo
    }

    /**
     * 登录监听器
     */
    private inner class LoginListener(
        private val callback: (Boolean, String?, Map<String, Any>?) -> Unit
    ) : IUiListener {
        override fun onComplete(response: Any?) {
            if (response is JSONObject) {
                try {
                    val openId = response.optString("openid")
                    val accessToken = response.optString("access_token")
                    val expiresIn = response.optLong("expires_in")
                    mTencent?.openId = openId
                    mTencent?.setAccessToken(accessToken, expiresIn.toString())
                    val userInfo = mapOf(
                        "openid" to openId,
                        "access_token" to accessToken,
                        "expires_in" to expiresIn
                    )
                    HiLogger.i("QQ登录成功，OpenID: $openId")
                    callback(true, null, userInfo)
                } catch (e: Exception) {
                    HiLogger.e("解析登录响应失败: ${e.message}")
                    callback(false, "解析登录响应失败", null)
                }
            } else {
                HiLogger.e("QQ登录返回数据格式错误")
                callback(false, "登录返回数据格式错误", null)
            }
        }

        override fun onError(error: UiError?) {
            HiLogger.e("QQ登录失败: ${error?.errorMessage} , errorCode: ${error?.errorCode}")
            callback(false, error?.errorMessage ?: "登录失败", null)
        }

        override fun onCancel() {
            HiLogger.i("用户取消QQ登录")
            callback(false, "用户取消登录", null)
        }

        override fun onWarning(code: Int) {
            HiLogger.w("收到警告信息，Code: $code")
        }
    }

    /**
     * 用户信息监听器
     */
    private inner class UserInfoListener(
        private val callback: (Boolean, String?, Map<String, Any>?) -> Unit
    ) : IUiListener {
        override fun onComplete(response: Any?) {
            HiLogger.d("获取用户信息成功 : ${response.toString()}")
            if (response is JSONObject) {
                try {
                    val userInfo = mapOf(
                        "nickname" to response.optString("nickname"),
                        "figureurl" to response.optString("figureurl"),
                        "figureurl_1" to response.optString("figureurl_1"),
                        "figureurl_2" to response.optString("figureurl_2"),
                        "figureurl_qq_1" to response.optString("figureurl_qq_1"),
                        "figureurl_qq_2" to response.optString("figureurl_qq_2"),
                        "gender" to response.optString("gender"),
                        "is_yellow_vip" to response.optString("is_yellow_vip"),
                        "vip" to response.optString("vip"),
                        "yellow_vip_level" to response.optString("yellow_vip_level"),
                        "level" to response.optString("level"),
                        "is_yellow_year_vip" to response.optString("is_yellow_year_vip")
                    )
                    HiLogger.i("成功获取QQ用户信息：${response.optString("nickname")}")
                    callback(true, null, userInfo)
                } catch (e: Exception) {
                    HiLogger.e("解析用户信息失败: ${e.message}")
                    callback(false, "解析用户信息失败", null)
                }
            } else {
                HiLogger.e("获取用户信息返回数据格式错误")
                callback(false, "获取用户信息返回数据格式错误", null)
            }
        }

        override fun onError(error: UiError?) {
            HiLogger.e("获取用户信息失败: ${error?.errorMessage}")
            callback(false, error?.errorMessage ?: "获取用户信息失败", null)
        }

        override fun onCancel() {
            HiLogger.e("获取用户信息已取消")
            callback(false, "获取用户信息已取消", null)
        }

        override fun onWarning(code: Int) {
            HiLogger.w("收到警告信息，Code: $code")
        }
    }
}