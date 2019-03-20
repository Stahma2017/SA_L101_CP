package com.example.sa_l101_cp

import android.content.ContentProvider
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.text.TextUtils
import android.util.Log

class MyContactsProvider:ContentProvider {

    val LOG_TAG = "myLogs"

    companion object {
        val DB_NAME = "mydb"
        val DB_VERSION = 1

        val CONTACT_TABLE = "contacts"

        val CONTACT_ID = "_id"
        val CONTACT_NAME = "name"
        val CONTACT_EMAIL = "email"

        val DB_CREATE = "create table $CONTACT_TABLE(" +
                "$CONTACT_ID integer primary key autoincrement, " +
                "$CONTACT_NAME text, " +
                "$CONTACT_EMAIL text);"

        val AUTHORITY = "ru.startandroid.providers.AdressBook"

        val CONTACT_PATH = "contacts"

        val CONTACT_CONTENT_URI = Uri.parse("content://$AUTHORITY/CONTACT_PATH")

        val CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.$CONTACT_PATH"

        val URI_CONTACTS = 1

        val URI_CONTACTS_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, CONTACT_PATH, URI_CONTACTS)
            uriMatcher.addURI(AUTHORITY, "$CONTACT_PATH/#", URI_CONTACTS_ID)
        }
    }
    lateinit var dbHelper : DBHelper
    lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        Log.d(LOG_TAG, "onCreate")
        dbHelper = DBHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
Log.d(LOG_TAG, "query, $uri")
        when(uriMatcher.match(uri)){
            URI_CONTACTS -> {
                Log.d(LOG_TAG, "URI_CONTACTS")
                if (TextUtils.isEmpty(sortOrder)){
                    sortOrder = "$CONTACT_NAME ASC"
                }
            }

        }
    }
}