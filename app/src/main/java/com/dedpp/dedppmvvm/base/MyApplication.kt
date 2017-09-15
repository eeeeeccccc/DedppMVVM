package com.dedpp.dedppmvvm.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.blankj.utilcode.util.Utils
import com.dedpp.dedppmvvm.data.realm.Migration
import com.dedpp.dedppmvvm.data.realm.MyModule
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * MyApplication
 * Created by linzhixin on 2017/8/25.
 */
class MyApplication : Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        @JvmStatic
        fun getAppContent(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Utils.init(this)

//        SyncService.openSyncService(context, 0)//FIXME

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name(Migration.REALM_NAME)
                .schemaVersion(Migration.REALM_VERSION_CODE)
                .modules(MyModule())
                .migration(Migration())
                .build()
        Realm.setDefaultConfiguration(config)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}