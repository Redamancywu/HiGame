package com.neil.higamesdk.account.login

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
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
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager?.getCredential(
                request = request,
                context = activity
            )
            if (result != null) {
                handleSignIn(result, callback)
            }
        } catch (e: GetCredentialException) {
            handleFailure(e, callback)
        }
    }

    /**
     * 处理登录成功的结果
     */
    fun handleSignIn(
        result: GetCredentialResponse,
        callback: (Result<GoogleSignInResult>) -> Unit
    ) {
        // 处理成功返回的凭证
        val credential = result.credential

        when (credential) {
            // 处理 PublicKeyCredential
            is PublicKeyCredential -> {
                // 发送 authenticationResponseJson 到服务器进行验证
                val responseJson = credential.authenticationResponseJson
            }

            // 处理 PasswordCredential
            is PasswordCredential -> {
                // 发送 ID 和密码到服务器进行验证
                val username = credential.id
                val password = credential.password
            }

            // 处理 GoogleIdTokenCredential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // 使用 GoogleIdTokenCredential 对象并提取 ID 进行验证
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        // 通过后台服务器验证 ID Token
                        val idTokenString = googleIdTokenCredential.idToken
                        val displayName = googleIdTokenCredential.displayName
                        val phoneNumber = googleIdTokenCredential.phoneNumber
                        val familyName = googleIdTokenCredential.familyName
                        val givenName = googleIdTokenCredential.givenName
                        val id = googleIdTokenCredential.id
                        //val verifier = GoogleIdTokenVerifier()  // 需要创建一个验证器
                        //  val idToken = verifier.verify(idTokenString)

                        // 获取稳定的账户标识符
                        //  val subject = idToken?.payload?.subject
                        callback(
                            Result.success(
                                GoogleSignInResult(
                                    idToken = idTokenString,
                                    displayName = displayName,
                                    accountName = id,
                                    givenName = givenName,
                                    familyName = familyName,
                                    userId = id
                                )
                            )
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        HiLogger.e("Received an invalid Google ID token response", e)
                    }
                } else {
                    // 处理无法识别的自定义凭证类型
                    HiLogger.e("Unexpected type of credential")
                }
            }

            else -> {
                // 处理任何未识别的凭证类型
                HiLogger.e("Unexpected type of credential")
            }
        }
    }

    fun handleFailure(e: GetCredentialException, callback: (Result<GoogleSignInResult>) -> Unit) {
        when (e) {
            is NoCredentialException -> {
                HiLogger.e("No credentials found", e)
            }

            is GetCredentialCancellationException -> {
                HiLogger.e("User canceled the operation", e)
            }

            is GetCredentialInterruptedException -> {
                HiLogger.e("Operation interrupted", e)
            }

            is GetCredentialProviderConfigurationException -> {
                HiLogger.e("Provider configuration error", e)
            }

            is GetCredentialUnknownException -> {
                HiLogger.e("Unknown error", e)
            }

            is GetCredentialCustomException -> {
                HiLogger.e("Custom error: ${e.message}", e)
            }

            else -> {
                HiLogger.e("Authentication failed: ${e.message}", e)
            }
        }
        callback(Result.failure(e))
    }

    /**
     * Google 登录结果数据类
     */
    data class GoogleSignInResult(
        val userId :String,
        val idToken: String,
        val accountName: String,
        val displayName: String?,
        val givenName: String?,
        val familyName: String?
    )
}

