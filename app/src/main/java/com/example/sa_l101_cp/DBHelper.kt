package com.example.sa_l101_cp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sa_l101_cp.MyContactsProvider.Companion.CONTACT_EMAIL
import com.example.sa_l101_cp.MyContactsProvider.Companion.CONTACT_NAME
import com.example.sa_l101_cp.MyContactsProvider.Companion.CONTACT_TABLE
import com.example.sa_l101_cp.MyContactsProvider.Companion.DB_NAME
import com.example.sa_l101_cp.MyContactsProvider.Companion.DB_VERSION

class DBHelper(context: Context):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyContactsProvider.DB_CREATE)
        val cv = ContentValues()
        for (i in 1..3){
            cv.put(CONTACT_NAME, "name $i")
            cv.put(CONTACT_EMAIL, "email $i")
            db?.insert(CONTACT_TABLE, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}