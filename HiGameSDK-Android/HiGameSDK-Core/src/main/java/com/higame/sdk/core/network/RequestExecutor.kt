package com.higame.sdk.core.network

import com.higame.sdk.core.network.model.ApiResponse
import com.higame.sdk.core.network.error.NetworkError
import com.higame.sdk.core.network.cache.CacheManager
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.Serializable
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 网络请求执行器
 * @param T 响应数据类型
 * @param timeoutMillis 请求超时时间（毫秒）
 * @param retryStrategy 重试策略
 * @param cacheManager 缓存管理器
 * @param cacheKey 缓存键
 */
class RequestExecutor<T : Serializable>(
    private val timeoutMillis: Long = DEFAULT_TIMEOUT,
    private val retryStrategy: RetryStrategy? = null,
    private val cacheManager: CacheManager? = null,
    private val cacheKey: String? = null
) {
    companion object {
        private const val DEFAULT_TIMEOUT = 30000L
    }

    /**
     * 执行请求
     * @param request 请求函数
     * @return API响应
     */
    suspend fun execute(request: suspend () -> Response<ApiResponse<T>>): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                // 尝试从缓存获取数据
                cacheKey?.let { key ->
                    cacheManager?.get<ApiResponse<T>>(
                        key,
                        clazz = TODO(),
                        loadFromFile = TODO()
                    )?.let { return@withContext it }
                }

                var lastError: Throwable? = null
                var retryCount = 0

                while (isActive) {
                    try {
                        val response = withTimeout(timeoutMillis) { request() }
                        val apiResponse = response.body() ?: throw NetworkError.ParseError("响应体为空")

                        // 保存成功响应到缓存
                        if (apiResponse.isSuccess()) {
                            cacheKey?.let { key ->
                                cacheManager?.put(key, apiResponse)
                            }
                        }

                        return@withContext apiResponse
                    } catch (e: CancellationException) {
                        throw e // 直接抛出取消异常
                    } catch (e: TimeoutCancellationException) {
                        throw NetworkError.TimeoutError("请求超时")
                    } catch (e: Throwable) {
                        lastError = e

                        // 检查是否需要重试
                        if (retryStrategy?.shouldRetry(e) == true && isActive) {
                            retryCount++
                            val delay = retryStrategy.getRetryDelay(retryCount)
                            delay(delay)
                            continue
                        }
                        break
                    }
                }

                // 转换并抛出最后的错误
                throw when (lastError) {
                    is NetworkError -> lastError
                    is UnknownHostException -> NetworkError.UnknownError("网络连接失败")
                    is SocketTimeoutException -> NetworkError.TimeoutError("请求超时")
                    else -> NetworkError.UnknownError("未知错误")
                }
            } catch (e: CancellationException) {
                throw e // 保持协程取消的传播
            } catch (e: NetworkError) {
                throw e // 直接抛出已转换的网络错误
            } catch (e: Throwable) {
                throw NetworkError.UnknownError("未知错误")
            }
        }
    }
}

/**
 * 重试策略接口
 */
interface RetryStrategy {
    /**
     * 判断是否需要重试
     * @param error 错误信息
     * @return 是否重试
     */
    fun shouldRetry(error: Throwable): Boolean

    /**
     * 获取重试延迟时间（毫秒）
     * @param retryCount 当前重试次数
     * @return 延迟时间
     */
    fun getRetryDelay(retryCount: Int): Long
}