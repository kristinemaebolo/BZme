package com.example.bzme.Helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.text.DateFormat
import java.text.ParseException
import java.time.format.DateTimeParseException


class Data {
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateTime(fromTime: String, toTime: String): Boolean {

        // Added try catch to prevent the app from crashing if time is invalid
        return try {
            val ftime = LocalTime.parse(fromTime)
            val ttime = LocalTime.parse(toTime)
            var fromStr = fromTime.split(':')
            var fromHour = fromStr[0].toInt()
            val fromMinute = fromStr[1].toInt()
            var from = LocalTime.of(fromHour, fromMinute)
            from.isBefore(ttime) and LocalTime.now().isAfter(ftime)
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun convertDateFromDb(date: Long) : String {
        val date = Date(date)
        return SimpleDateFormat("MM/dd/yyyy").format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime() : String {
        val cal = Calendar.getInstance()
        return SimpleDateFormat("MM/dd/YYYY HH:mm:ss").format(cal.time)
    }

    fun convertDateTimeToTime(dateTime: String): String {
        var date = Date.parse(dateTime)
        val df = SimpleDateFormat("HH:mm").format(date)
        return df.toString()

    }

    fun validateDate(date: String): Boolean{

        return try {
            val df: DateFormat = SimpleDateFormat("MM/dd/yyyy")
            df.setLenient(false)
            df.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }
}