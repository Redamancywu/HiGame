package com.higame.sdk.core.config

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// 主配置类
data class HiGameConfig(
    @SerializedName("sdk_version") var sdkVersion: String,
    @SerializedName("game_name") var gameName: String,
    @SerializedName("debug_mode") var debugMode: Boolean,
    @SerializedName("features") var features: Features? = null,
    @SerializedName("server") var server: Server? = null,
    @SerializedName("privacy") var privacy: Privacy? = null
){
    companion object {
        /**
         * 从Json字符串中解析HiGameConfig对象
         * @param jsonStr Json字符串
         * @return HiGameConfig对象
         */
        fun fromJson(jsonStr: String): HiGameConfig {
            val gson = Gson()
            return gson.fromJson(jsonStr, HiGameConfig::class.java)
        }
    }
}

// 功能模块类
data class Features(
    @SerializedName("auth") var auth: Auth? = null,
    @SerializedName("payment") var payment: Payment? = null,
    @SerializedName("ads") var ads: Ads? = null,
    @SerializedName("account") var account: Account? = null,
    @SerializedName("social") var social: Social? = null,
    @SerializedName("analytics") var analytics: Analytics? = null,
    @SerializedName("localization") var localization: Localization? = null,
    @SerializedName("push_notifications") var pushNotifications: PushNotifications? = null,
    @SerializedName("cloud_services") var cloudServices: CloudServices? = null
)

// 用户认证模块
data class Auth(
    @SerializedName("oauth_providers") var oauthProviders: List<String>? = null,
    @SerializedName("token_expiration_time") var tokenExpirationTime: Int? = null,
    @SerializedName("encryption_key") var encryptionKey: String? = null
)

// 支付模块
data class Payment(
    @SerializedName("merchant_id") var merchantId: String? = null,
    @SerializedName("supported_payment_methods") var supportedPaymentMethods: List<String>? = null,
    @SerializedName("subscription_support") var subscriptionSupport: Boolean? = null,
    @SerializedName("sandbox_mode") var sandboxMode: Boolean? = null
)

// 广告模块
data class Ads(
    @SerializedName("ad_platform") var adPlatform: String? = null,
    @SerializedName("ad_unit_ids") var adUnitIds: AdUnitIds? = null,
    @SerializedName("frequency_capping") var frequencyCapping: FrequencyCapping? = null
)

// 广告单元 ID
data class AdUnitIds(
    @SerializedName("adPosId") var adPosId: String? = null,
    @SerializedName("banner") var banner: String? = null,
    @SerializedName("interstitial") var interstitial: String? = null,
    @SerializedName("rewarded_video") var rewardedVideo: String? = null,
    @SerializedName("native") var native: String? = null
)

// 广告频率控制
data class FrequencyCapping(
    @SerializedName("max_interstitial_per_hour") var maxInterstitialPerHour: Int? = null,
    @SerializedName("max_rewarded_video_per_day") var maxRewardedVideoPerDay: Int? = null
)

// 账户模块
data class Account(
    @SerializedName("auto_login") var autoLogin: Boolean? = null,
    @SerializedName("guest_login_enabled") var guestLoginEnabled: Boolean? = null,
    @SerializedName("friend_system_enabled") var friendSystemEnabled: Boolean? = null,
    @SerializedName("leaderboard_enabled") var leaderboardEnabled: Boolean? = null,
    @SerializedName("google_client") var googleClient: String? = null,
    @SerializedName("qq_app_id") var qqAppId: String? = null,
    @SerializedName("qq_app_key") var qqAppKey: String? = null
)

// 社交模块
data class Social(
    @SerializedName("share_providers") var shareProviders: List<String>? = null,
    @SerializedName("invite_bonus") var inviteBonus: InviteBonus? = null
)

// 邀请奖励
data class InviteBonus(
    @SerializedName("points") var points: Int? = null,
    @SerializedName("currency") var currency: String? = null
)

// 数据分析模块
data class Analytics(
    @SerializedName("api_key") var apiKey: String? = null,
    @SerializedName("event_tracking_enabled") var eventTrackingEnabled: Boolean? = null,
    @SerializedName("realtime_dashboard_url") var realtimeDashboardUrl: String? = null
)

// 多语言支持模块
data class Localization(
    @SerializedName("supported_languages") var supportedLanguages: List<String>? = null,
    @SerializedName("default_language") var defaultLanguage: String? = null
)

// 推送通知模块
data class PushNotifications(
    @SerializedName("enabled") var enabled: Boolean? = null,
    @SerializedName("fcm_sender_id") var fcmSenderId: String? = null
)

// 云服务模块
data class CloudServices(
    @SerializedName("cloud_storage_enabled") var cloudStorageEnabled: Boolean? = null,
    @SerializedName("sync_frequency_minutes") var syncFrequencyMinutes: Int? = null
)

// 服务器配置
data class Server(
    @SerializedName("api_host") var apiHost: String? = null,
    @SerializedName("cdn_host") var cdnHost: String? = null
)

// 隐私合规模块
data class Privacy(
    @SerializedName("gdpr_compliance") var gdprCompliance: Boolean? = null,
    @SerializedName("ccpa_compliance") var ccpaCompliance: Boolean? = null,
    @SerializedName("att_framework_compliance") var attFrameworkCompliance: Boolean? = null
)