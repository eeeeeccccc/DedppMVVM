package com.dedpp.dedppmvvm.utils

import android.text.TextUtils
import android.util.Log
import com.dedpp.dedppmvvm.BuildConfig


/**
 * LogUtil
 * Created by Dedpp on 2017/8/25.
 */
class LogUtil {

    companion object {

        @JvmStatic
        fun logD(msg: String) {
            if (TextUtils.isEmpty(msg))
                return
            Log.d(if (BuildConfig.DEBUG) "dedpp" else Constant.LOG_TAG, msg)
        }

        @JvmStatic
        fun logE(msg: String) {
            if (TextUtils.isEmpty(msg))
                return
            Log.e(if (BuildConfig.DEBUG) "dedpp" else Constant.LOG_TAG, msg)
        }
    }

}