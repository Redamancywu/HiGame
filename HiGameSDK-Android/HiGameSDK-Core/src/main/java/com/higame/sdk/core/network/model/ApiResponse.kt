package com.higame.sdk.core.network.model

/**
 * API响应的统一包装类
 * @param T 响应数据的类型
 * @property code 响应码
 * @property message 响应消息
 * @property data 响应数据
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean = code == 0

    companion object {
        const val SUCCESS_CODE = 0
        
        /**
         * 创建一个成功的响应
         */
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(
            code = SUCCESS_CODE,
            message = "success",
            data = data
        )

        /**
         * 创建一个错误的响应
         */
        fun <T> error(code: Int, message: String): ApiResponse<T> = ApiResponse(
            code = code,
            message = message,
            data = null
        )
    }
}