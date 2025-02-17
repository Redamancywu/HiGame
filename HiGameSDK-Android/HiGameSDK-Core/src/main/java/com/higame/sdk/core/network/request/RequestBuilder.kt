package com.higame.sdk.core.network.request

import com.higame.sdk.core.network.NetworkConfig
import com.higame.sdk.core.network.cache.CacheManager
import com.higame.sdk.core.network.model.ApiResponse
import com.higame.sdk.core.network.retry.RetryStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import retrofit2.Response

/**
 * 网络请求构建器
 */
class RequestBuilder<T> where T : Any { // 不强制 T 为 Serializable
    private var retryStrategy: RetryStrategy? = null
    private var cacheKey: String? = null
    private var cacheManager: CacheManager? = null
    private var timeoutMillis: Long = DEFAULT_TIMEOUT

    /**
     * 设置重试策略
     */
    fun setRetryStrategy(strategy: RetryStrategy): RequestBuilder<T> {
        this.retryStrategy = strategy
        return this
    }

    /**
     * 设置缓存策略
     */
    fun setCache(cacheManager: CacheManager, key: String): RequestBuilder<T> {
        this.cacheManager = cacheManager
        this.cacheKey = key
        return this
    }

    /**
     * 设置请求超时时间
     */
    fun setTimeout(timeoutMillis: Long): RequestBuilder<T> {
        this.timeoutMillis = timeoutMillis
        return this
    }

    /**
     * 执行请求
     */
    suspend fun execute(request: suspend () -> Response<ApiResponse<T>>): ApiResponse<out Any?> {
        // 尝试从缓存获取数据
        cacheKey?.let { key ->
            cacheManager?.get(key, ApiResponse::class.java)?.let { return it }
        }

        var lastError: Throwable? = null
        var retryCount = 0

        while (true) {
            try {
                val response = withTimeout(timeoutMillis) { request() }
                val apiResponse = response.body() ?: throw IllegalStateException("Response body is null")

                // 保存成功响应到缓存
                if (apiResponse.isSuccess()) {
                    cacheKey?.let { key ->
                        cacheManager?.put(key, apiResponse)
                    }
                }

                return apiResponse
            } catch (e: Throwable) {
                lastError = e

                // 检查是否需要重试
                if (retryStrategy?.shouldRetry(e) == true) {
                    retryCount++
                    delay(retryStrategy?.getRetryDelay() ?: 0)
                    continue
                }
                break
            }
        }

        // 重试失败，返回错误响应
        return ApiResponse.error(-1, lastError?.message ?: "Unknown error")
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 30_000L // 30秒

        /**
         * 创建API服务接口实例
         */
        fun <T> createService(serviceClass: Class<T>): T = NetworkConfig.createService(serviceClass)
    }
}
