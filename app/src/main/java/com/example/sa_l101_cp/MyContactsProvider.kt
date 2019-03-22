package com.example.sa_l101_cp

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import java.lang.IllegalArgumentException

class MyContactsProvider:ContentProvider() {

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

        val CONTACT_CONTENT_ITEM_TYPE = ("vnd.android.cursor.item/vnd.$AUTHORITY.CONTACT_PATH")

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
        var sortOrderVar = sortOrder
        var selectionVar = selection
        when (uriMatcher.match(uri)) {
            URI_CONTACTS -> {
                Log.d(LOG_TAG, "URI_CONTACTS")
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrderVar = "$CONTACT_NAME ASC"
                }
            }
            URI_CONTACTS_ID -> {
                val id = uri.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, $id")
                selectionVar = if (TextUtils.isEmpty(selection)){
                    "$CONTACT_ID = $id"
                } else {
                    "$selection AND $CONTACT_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URL: $uri")
        }
        db = dbHelper.writableDatabase
        val cursor: Cursor = db.query(CONTACT_TABLE, projection, selectionVar,
            selectionArgs, null, null, sortOrderVar)
        cursor.setNotificationUri(context?.contentResolver, CONTACT_CONTENT_URI)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
       Log.d(LOG_TAG, "insert, $uri")
        if (uriMatcher.match(uri) != URI_CONTACTS){
            throw IllegalArgumentException("Wrong URI: $uri")
        }
        db = dbHelper.writableDatabase
        val rowID = db.insert(CONTACT_TABLE, null, values)
        val resultUri = ContentUris.withAppendedId(CONTACT_CONTENT_URI, rowID)
        context?.contentResolver?.notifyChange(resultUri, null)
        return resultUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        Log.d(LOG_TAG, "delete, $uri")
        var selectionVar = selection
        when(uriMatcher.match(uri)){
            URI_CONTACTS -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_CONTACTS_ID -> {
                val id = uri.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, $id")
                selectionVar = if (TextUtils.isEmpty(selection)){
                    "$CONTACT_ID = $id"
                } else{
                    "$selectionVar AND $CONTACT_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: $uri")
        }
        db = dbHelper.writableDatabase
        val cnt = db.delete(CONTACT_TABLE, selectionVar, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return cnt
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        var selectionVar = selection
        Log.d(LOG_TAG, "update, $uri")
        when (uriMatcher.match(uri)){
            URI_CONTACTS -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_CONTACTS_ID -> {
                val id = uri.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, $id")
                selectionVar = if (TextUtils.isEmpty(selection)){
                    "$CONTACT_ID = $id"
                } else {
                    "$selection AND $CONTACT_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: $uri")
        }
        db = dbHelper.writableDatabase
        val cnt = db.update(CONTACT_TABLE, values, selectionVar, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return cnt
    }

    override fun getType(uri: Uri): String? {
        Log.d(LOG_TAG, "getType, $uri")
        return when(uriMatcher.match(uri)){
            URI_CONTACTS -> CONTACT_CONTENT_TYPE
            URI_CONTACTS_ID -> CONTACT_CONTENT_ITEM_TYPE
            else -> null
        }
    }
}