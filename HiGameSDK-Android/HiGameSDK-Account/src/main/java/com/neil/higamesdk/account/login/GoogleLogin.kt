package com.neil.higamesdk.account.login

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.higame.sdk.core.callback.HiGameModuleInitCallback
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.utils.log.HiLogger

class GoogleLogin private constructor() {

    companion object {
        @Volatile
        private var instance: GoogleLogin? = null

        fun getInstance(): GoogleLogin =
            instance ?: synchronized(this) {
                instance ?: GoogleLogin().also { instance = it }
            }
    }

    private lateinit var config: HiGameConfig
    private lateinit var googleClientId: String
    private lateinit var credentialManager: CredentialManager
    private lateinit var initCallback: HiGameModuleInitCallback

    /**
     * 初始化回调
     */
    fun setCallback(callback: HiGameModuleInitCallback) {
        this.initCallback = callback
        HiLogger.d("GoogleLogin setCallback ")
    }

    /**
     * 初始化 Google 登录模块
     */
    fun init(context: Context, config: HiGameConfig) {
        this.config = config
        this.googleClientId = config.features?.account?.googleClient.toString()
        this.credentialManager = CredentialManager.create(context)
        HiLogger.d("GoogleLogin init googleClientId :$googleClientId")
        // 验证配置参数
        if (!googleClientId.isNullOrEmpty()) {
            val successMessage = "Google Login initialized successfully"
            val successData = mapOf(
                "googleClientId" to googleClientId,
                "status" to "initialized"
            )
            HiLogger.i(successMessage)
            initCallback.onSuccess(successMessage, successData)
        } else {
            val errorMessage = "Google Server Client ID must be configured in HiGameConfig"
            HiLogger.e(errorMessage)
            initCallback.onFailed(400, errorMessage)
        }
    }

    /**
     * 启动 Google 登录流程
     */
    suspend fun signIn(activity: Activity, callback: (Result<GoogleSignInResult>) -> Unit) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(googleClientId ?: "")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            // .setNonce(config?.googleSignInNonce ?: "") // 如果有配置 nonce
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager?.getCredential(
                request = request,
                context = activity
            )
            handleSignInResult(result, callback)
        } catch (e: Exception) {
            handleSignInError(e, callback)
        }
    }

    /**
     * 处理登录成功的结果
     */
    private fun handleSignInResult(
        result: GetCredentialResponse?,
        callback: (Result<GoogleSignInResult>) -> Unit
    ) {
        try {
            if (result == null) {
                callback(Result.failure(IllegalStateException("Credential response is null")))
                return
            }

            val credential = result.credential as? GoogleIdTokenCredential
            if (credential != null) {
                val signInResult = GoogleSignInResult(
                    idToken = credential.idToken,
                    accountName = credential.id,
                    displayName = credential.displayName,
                    givenName = credential.givenName,
                    familyName = credential.familyName
                )
                callback(Result.success(signInResult))
            } else {
                callback(Result.failure(IllegalStateException("Invalid credential type")))
            }
        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    /**
     * 处理登录失败的错误
     */
    private fun handleSignInError(
        e: Exception,
        callback: (Result<GoogleSignInResult>) -> Unit
    ) {
        val errorMessage = when (e) {
            is NoCredentialException -> "No credentials found"
            is GetCredentialCancellationException -> "User canceled the operation"
            is GetCredentialInterruptedException -> "Operation interrupted"
            is GetCredentialProviderConfigurationException -> "Provider configuration error"
            is GetCredentialUnknownException -> "Unknown error"
            is GetCredentialCustomException -> "Custom error: ${e.message}"
            else -> "Authentication failed: ${e.message}"
        }
        callback(Result.failure(RuntimeException(errorMessage, e)))
    }

    /**
     * Google 登录结果数据类
     */
    data class GoogleSignInResult(
        val idToken: String,
        val accountName: String,
        val displayName: String?,
        val givenName: String?,
        val familyName: String?
    )
}