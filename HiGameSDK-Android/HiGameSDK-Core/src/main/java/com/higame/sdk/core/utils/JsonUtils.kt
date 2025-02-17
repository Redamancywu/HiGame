package com.higame.sdk.core.utils

import android.content.Context
import org.json.JSONObject

object JsonUtils {
    fun readJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun getJsonObjectFromAssets(context: Context, fileName: String): JSONObject? {
        val jsonString = readJsonFromAssets(context, fileName)
        return jsonString?.let {
            try {
                JSONObject(it)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}