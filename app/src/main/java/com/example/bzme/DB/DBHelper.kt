package com.example.bzme.DB
import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.bzme.Helper.Data
import com.example.bzme.Model.Activities
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(var context: Context?) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
//
    companion object {
        private val DATABASE_VER = 2
        private val DATABASE_NAME = "bzme.db"
        private val TABLE_NAME_MAIN = "activity"
        private val COL_ID = "id"
        private val COL_TITLE = "title"
        private val COL_REPLY = "reply"
        private val COL_STATUS = "status"
        private val COL_FROM_TIME = "activity_from_time"
        private val COL_TO_TIME = "activity_to_time"
        private val COL_DESCRIPTION = "description"
        private val COL_DATE = "created_at"

        private val TABLE_PHONE_NUM = "phone_num"
        private val PHONE_NUM_ID = "phone_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_MAIN = "CREATE TABLE $TABLE_NAME_MAIN " +
                "($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_TITLE TEXT, " +
                "$COL_REPLY TEXT, " +
                "$COL_FROM_TIME TEXT, " +
                "$COL_TO_TIME TEXT, " +
                "$COL_STATUS INTEGER, " +
                "$COL_DESCRIPTION TEXT," +
                "$COL_DATE LONG)"
        db?.execSQL(CREATE_TABLE_MAIN)

//        val PHONE_NUMS = "CREATE TABLE $TABLE_PHONE_NUM\n" +
//                "( $PHONE_NUM_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                "  CONSTRAINT fk_$COL_ID\n" +
//                "    FOREIGN KEY ($COL_ID)\n" +
//                "    REFERENCES $TABLE_NAME_MAIN($COL_ID)\n" +
//                "    ON DELETE CASCADE\n" +
//                ");"
//
//        db?.execSQL(PHONE_NUMS)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_MAIN")
        onCreate(p0)
    }

    @SuppressLint("Range")
    fun listAllActivities() : MutableList<Activities>{
        val activities : MutableList<Activities> = ArrayList()
        val query = "SELECT * FROM $TABLE_NAME_MAIN"
        val db = this.readableDatabase
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()){
            do{
                val activiy = Activities()
                activiy.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                activiy.title = result.getString(result.getColumnIndex(COL_TITLE))
                activiy.from_time = result.getString(result.getColumnIndex(COL_FROM_TIME))
                activiy.to_time = result.getString(result.getColumnIndex(COL_TO_TIME))
                activiy.reply = result.getString(result.getColumnIndex(COL_REPLY))

                activiy.created_at = result.getString(result.getColumnIndex(COL_DATE)).toLong()
                activiy.status = result.getString(result.getColumnIndex(COL_STATUS)).toInt()
                activities.add(activiy)
            }while(result.moveToNext())
        }
        db.close()
        return activities
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun hasOnGoingActivity() : MutableList<Activities>{
        var activities : MutableList<Activities> = ArrayList()
        val currentTime = Data().getCurrentDateTime()
        val query = "SELECT * from $TABLE_NAME_MAIN " +
                "WHERE $COL_FROM_TIME <= '$currentTime' AND $COL_TO_TIME >= '$currentTime' limit 1"
        Log.d("OnGoing A Query: ", query)
        Log.d("Time Now: ", currentTime)

        var db = this.readableDatabase
        var res = db.rawQuery(query,null)
        var reply = "Sorry I'm busy."
        if(res.moveToFirst()){
            do{
                var to = Activities()
                to.id = res.getString(res.getColumnIndex(COL_ID)).toInt()
                to.title = res.getString(res.getColumnIndex(COL_TITLE))
                to.from_time = res.getString(res.getColumnIndex(COL_FROM_TIME))
                to.to_time = res.getString(res.getColumnIndex(COL_TO_TIME))
                to.reply = res.getString(res.getColumnIndex(COL_REPLY))
                to.created_at = res.getString(res.getColumnIndex(COL_DATE)).toLong()
                reply = res.getString(2).toString()
                activities.add(to)

            }while(res.moveToNext())
        }else{
            Toast.makeText(context,"No Schedules",Toast.LENGTH_LONG).show()
        }

        db.close()
        return activities
    }


    fun addActivity(title : String, reply : String,to_time : String, from_time : String, createDate: String) : Long{
        val db = this.writableDatabase
        val values = ContentValues()
            values.put(COL_REPLY, reply)
            values.put(COL_TITLE, title)
            values.put(COL_STATUS, 1)
            values.put(COL_TO_TIME, "$createDate $to_time")
            values.put(COL_FROM_TIME, "$createDate $from_time")
            values.put(COL_DATE,Date.parse(createDate))
        var res = db.insert(TABLE_NAME_MAIN, null, values)

        db.close()
        return res
    }

    fun deleteActivity(id: Int){
        val db = this.writableDatabase
        db.delete(TABLE_NAME_MAIN,"id=$id",null)
        db.close()
    }

    fun updateActivity(id: String, title : String, reply : String,to_time : String, from_time : String, createDate: String){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_REPLY, reply)
        values.put(COL_TITLE, title)
        values.put(COL_STATUS, 1)
        values.put(COL_TO_TIME, "$createDate $to_time")
        values.put(COL_FROM_TIME, "$createDate $from_time")
        values.put(COL_DATE,Date.parse(createDate))
        db.update(TABLE_NAME_MAIN,values,"id=?",arrayOf(id))
    }
}
