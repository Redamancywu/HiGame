package com.higame.sdk.core

import android.app.Application
import android.content.res.Configuration
import com.higame.sdk.core.utils.ApplicationUtils
import com.higame.sdk.core.utils.ContextUtils
import com.higame.sdk.core.utils.log.HiLogger

class HiGameSDKApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationUtils.init(this)
        ContextUtils.init(this)
        HiGameLifecycle.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        HiGameLifecycle.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        HiGameLifecycle.onLowMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        HiGameLifecycle.onTrimMemory(level)
    }

}