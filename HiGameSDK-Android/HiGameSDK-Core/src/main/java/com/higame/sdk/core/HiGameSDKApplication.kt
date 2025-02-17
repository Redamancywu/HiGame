package com.higame.sdk.core

import android.app.Application
import com.higame.sdk.core.utils.ContextUtils
import com.higame.sdk.core.utils.log.HiLogger

class HiGameSDKApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextUtils.init(this)

    }
}