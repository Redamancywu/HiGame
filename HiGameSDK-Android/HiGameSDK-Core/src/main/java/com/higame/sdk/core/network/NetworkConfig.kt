package com.higame.sdk.core.network

import com.google.gson.GsonBuilder
import com.higame.sdk.core.network.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络请求配置类
 */
object NetworkConfig {
    private const val DEFAULT_TIMEOUT = 30L
    private var baseUrl: String = ""

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    /**
     * 初始化网络配置
     * @param baseUrl API基础URL
     */
    fun init(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    /**
     * 创建API服务接口实例
     * @param serviceClass API服务接口类
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}