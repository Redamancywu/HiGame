package com.higame.sdk.core.utils

import android.content.Context
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.utils.log.HiLogger

/**
 * SDK配置加载工具类
 * 负责从assets目录加载和解析配置文件
 */
object ConfigLoader {
    private const val CONFIG_FILE_NAME = "higame_config.json"
    
    /**
     * 从assets目录加载配置文件
     * @param context Android上下文
     * @return com.higame.sdk.core.config.HiGameConfig 配置对象
     * @throws IllegalStateException 当配置文件加载或解析失败时抛出
     */
    fun loadConfig(context: Context): HiGameConfig {
        try {
            // 从assets目录读取配置文件
            val inputStream = context.assets.open(CONFIG_FILE_NAME)
            // 读取文件内容
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            // 解析JSON内容为HiGameConfig对象
            return HiGameConfig.fromJson(jsonString)
        } catch (e: Exception) {
            val errorMessage = "Failed to load config from $CONFIG_FILE_NAME: ${e.message}"
            HiLogger.e(errorMessage, e)
            throw IllegalStateException(errorMessage)
        }
    }
}