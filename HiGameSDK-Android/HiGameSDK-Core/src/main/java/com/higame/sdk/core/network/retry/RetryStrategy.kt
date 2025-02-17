package com.higame.sdk.core.network.retry

import com.higame.sdk.core.network.error.NetworkError

/**
 * 请求重试策略
 */
class RetryStrategy(
    private val maxRetries: Int = DEFAULT_MAX_RETRIES,
    private val retryDelay: Long = DEFAULT_RETRY_DELAY
) {
    private var retryCount = 0

    /**
     * 判断是否可以重试
     * @param error 网络错误
     * @return 是否可以重试
     */
    fun shouldRetry(error: Throwable): Boolean {
        if (retryCount >= maxRetries) return false

        return when (error) {
            is NetworkError.ConnectionError,
            is NetworkError.TimeoutError -> {
                retryCount++
                true
            }
            else -> false
        }
    }

    /**
     * 获取重试延迟时间
     * @return 延迟时间（毫秒）
     */
    fun getRetryDelay(): Long = retryDelay * retryCount

    /**
     * 重置重试次数
     */
    fun reset() {
        retryCount = 0
    }

    companion object {
        const val DEFAULT_MAX_RETRIES = 3
        const val DEFAULT_RETRY_DELAY = 1000L // 1秒
    }
}