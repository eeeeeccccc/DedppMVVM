package com.dedpp.dedppmvvm.data.realm;

import android.support.annotation.NonNull;

import com.dedpp.dedppmvvm.utils.LogUtil;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Migration
 * Created by Dedpp on 17/9/5.
 */
public class Migration implements RealmMigration {

    public static final String REALM_NAME = "realm.ctbaba.cargotracking";
    public static final long REALM_VERSION_CODE = 1;

    @Override
    public void migrate(@NonNull final DynamicRealm realm, long oldVersion, long newVersion) {
        LogUtil.logD(this.getClass().getSimpleName() + " oldVersion ===== " + oldVersion);
        LogUtil.logD(this.getClass().getSimpleName() + " newVersion ===== " + newVersion);
        RealmSchema schema = realm.getSchema();

        // Migrate from version 1 to version 2
//        if (oldVersion == 1) {
//            LogUtil.logD("Migrate from version 1 to version 2");
//            oldVersion++;
//
//            schema.get("ProductItem")
//                    .addField("start", String.class);
//        }
    }

}