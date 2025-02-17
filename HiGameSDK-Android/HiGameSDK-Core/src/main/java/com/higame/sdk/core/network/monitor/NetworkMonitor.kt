package com.higame.sdk.core.network.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

/**
 * 网络状态监听器
 */
class NetworkMonitor(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val listeners = mutableSetOf<NetworkStateListener>()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            notifyListeners(true)
        }

        override fun onLost(network: Network) {
            notifyListeners(false)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * 添加网络状态监听器
     * @param listener 监听器
     */
    fun addListener(listener: NetworkStateListener) {
        listeners.add(listener)
        listener.onNetworkStateChanged(isNetworkAvailable())
    }

    /**
     * 移除网络状态监听器
     * @param listener 监听器
     */
    fun removeListener(listener: NetworkStateListener) {
        listeners.remove(listener)
    }

    /**
     * 检查网络是否可用
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun notifyListeners(isAvailable: Boolean) {
        listeners.forEach { it.onNetworkStateChanged(isAvailable) }
    }

    /**
     * 释放资源
     */
    fun release() {
        listeners.clear()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * 网络状态监听接口
     */
    interface NetworkStateListener {
        /**
         * 网络状态变化回调
         * @param isAvailable 网络是否可用
         */
        fun onNetworkStateChanged(isAvailable: Boolean)
    }
}