package com.higame.sdk.core.network.error

import java.io.IOException

/**
 * 网络错误类型
 */
sealed class NetworkError : IOException() {
    /**
     * 网络连接错误
     */
    class ConnectionError(override val message: String? = "网络连接失败") : NetworkError()

    /**
     * 服务器错误
     */
    class ServerError(val code: Int, override val message: String?) : NetworkError()

    /**
     * 请求超时错误
     */
    class TimeoutError(override val message: String? = "请求超时") : NetworkError()

    /**
     * 数据解析错误
     */
    class ParseError(override val message: String? = "数据解析失败") : NetworkError()

    /**
     * 未知错误
     */
    class UnknownError(override val message: String? = "未知错误") : NetworkError()
}