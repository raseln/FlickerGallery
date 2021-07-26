package com.rasel.flickergallery.utils

import android.util.Log
import com.rasel.flickergallery.BuildConfig

internal object LoggerUtils {

    fun log(priority: Int, msg: String?, function: String? = null) {
        Log.println(priority, "FlickerGallery", "[$function] $msg")
    }

}

// Using getStackTrace may cause a little time lag
private inline val caller: String? get() = Throwable().stackTrace.let { if (it.size > 2) it[2].toString() else null }

// Print log message for debug build only
internal fun debugLogInfo(msg: Any?) = if (BuildConfig.DEBUG) logInfo(msg) else Unit

// Print log message
internal fun logInfo(msg: Any?) = LoggerUtils.log(Log.INFO, if (msg is String) msg else msg.toString(), caller)
