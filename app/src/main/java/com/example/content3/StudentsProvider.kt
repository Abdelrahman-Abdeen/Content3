package com.example.content3

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import java.lang.IllegalArgumentException
import java.util.HashMap
import java.util.jar.Attributes.Name


class StudentsProvider(): ContentProvider() {
    companion object {
        val PROVIDER_NAME = "com.example.content3.StudentsProvider"
        var URL = "content://" + PROVIDER_NAME + "/students"
        val CONTENT_URI = Uri.parse(URL)
        val _ID = "_id"
        val NAME = "name"
        val GRADE = "grade"
        private val STUDENTS_PROJECTION_MAP: HashMap<String, String>? = null
        val STUDENTS = 1
        val STUDENT_ID = 2
        val uriMatcher: UriMatcher? = null
        val DATABASE_NAME = "College"
        val STUDENTS_TABLE_NAME = "students"
        val DATABASE_VERSION = 1
        val CREATE_DB_TABLE =
            "CREATE TABLE " + STUDENTS_TABLE_NAME + "(+id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT NOT NULL, " + " grade TEXT NOT NULL);"
    }

    private var sUriMatcher = UriMatcher(UriMatcher.NO_MATCH);

    init {
        sUriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS)
        sUriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID)
    }

    private var db:SQLiteDatabase? =null

    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase,oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS "+ STUDENTS_TABLE_NAME)
            onCreate(db)
        }
    }


        override fun onCreate(): Boolean {
        val context = context
            val dbHelper = DatabaseHelper(context)

            db = dbHelper.writableDatabase
            return if(db==null)false else true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = STUDENTS_TABLE_NAME
        when(uriMatcher!!.match(uri)){
            STUDENT_ID -> qb.appendWhere(_ID + "=" +uri.pathSegments[1])
            else->{
                null
            }
        }
        if(sortOrder==null ||sortOrder===""){
            sortOrder= NAME
        }
        val c =qb.query(db,projection,selection,selectionArgs,null,null,sortOrder)

        c.setNotificationUri(context!!.contentResolver,uri)
        return c
    }

    override fun getType(uri: Uri): String? {
        when(uriMatcher!!.match(uri)){
            STUDENTS-> return "vnd.android.cursor.dir/vnd.example.students"
            STUDENT_ID-> return "vnd.android.cursor.item/vnd.example.students"
            else->throw IllegalArgumentException("Unsupported URI :$uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowID = db!!.insert(STUDENTS_TABLE_NAME,"",values)

        if(rowID>0){
            val _uri = ContentUris.withAppendedId(CONTENT_URI,rowID)
            context!!.contentResolver.notifyChange(uri , null)
            return _uri
        }
        throw SQLException("Failed to add a record into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var count=0
        when(uriMatcher!!.match(uri)){
            STUDENTS -> count = db!!.delete(STUDENTS_TABLE_NAME,selection,selectionArgs)
            STUDENT_ID->{
                val id = uri.pathSegments[1]
                count=db!!.delete(STUDENTS_TABLE_NAME, _ID+"="+id+ if(!TextUtils.isEmpty(selection))"AND ($selection)" else "",selectionArgs)
            }
            else ->throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri,null)
        return count

    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectoionArgs: Array<out String>?): Int {
        var count=0
        when(uriMatcher!!.match(uri)){
            STUDENTS->count = db!!.update(STUDENTS_TABLE_NAME,values,selection,selectoionArgs)
            STUDENT_ID->count = db!!.update(STUDENTS_TABLE_NAME,values, _ID+"="+ uri.pathSegments[1] + (if(!TextUtils.isEmpty(selection))"AND($selection)" else ""),selectoionArgs)
            else->throw  IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri,null)
        return count
    }


    }