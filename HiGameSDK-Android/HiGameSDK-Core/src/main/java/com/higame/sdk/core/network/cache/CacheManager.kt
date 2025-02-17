package com.higame.sdk.core.network.cache

import android.content.Context
import android.util.LruCache
import com.google.gson.Gson
import java.io.File
import java.io.Serializable

/**
 * 网络缓存管理器
 */
class CacheManager(context: Context) {
    private val memoryCache = LruCache<String, String>(DEFAULT_MEMORY_CACHE_SIZE) // 缓存数据存储为字符串（JSON）
    private val cacheDir: File = File(context.cacheDir, "network_cache")

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    /**
     * 保存缓存数据
     * @param key 缓存键
     * @param value 缓存值
     * @param saveToFile 是否保存到文件
     */
    fun <T> put(key: String, value: T, saveToFile: Boolean = false) {
        val json = Gson().toJson(value)  // 将对象转为JSON字符串
        memoryCache.put(key, json)

        if (saveToFile) {
            saveToFile(key, json)
        }
    }

    /**
     * 获取缓存数据
     * @param key 缓存键
     * @param clazz 目标类型
     * @param loadFromFile 是否从文件加载
     * @return 缓存数据
     */
    fun <T> get(key: String, clazz: Class<T>, loadFromFile: Boolean = false): T? {
        val memoryValue = memoryCache.get(key)
        if (memoryValue != null) {
            return Gson().fromJson(memoryValue, clazz)  // 从JSON字符串转回对象
        }

        return if (loadFromFile) {
            loadFromFile(key, clazz)
        } else null
    }

    /**
     * 移除缓存数据
     * @param key 缓存键
     * @param removeFile 是否删除文件
     */
    fun remove(key: String, removeFile: Boolean = false) {
        memoryCache.remove(key)
        if (removeFile) {
            getCacheFile(key).delete()
        }
    }

    /**
     * 清除所有缓存
     */
    fun clear() {
        memoryCache.evictAll()
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private fun saveToFile(key: String, value: String) {
        try {
            value.byteInputStream().use { input ->
                getCacheFile(key).outputStream().use { it.write(input.readBytes()) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun <T> loadFromFile(key: String, clazz: Class<T>): T? {
        return try {
            val file = getCacheFile(key)
            val json = file.readText()
            Gson().fromJson(json, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCacheFile(key: String): File = File(cacheDir, key)

    companion object {
        private const val DEFAULT_MEMORY_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    }
}
