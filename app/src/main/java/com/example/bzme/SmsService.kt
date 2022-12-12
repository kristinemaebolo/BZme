package com.example.bzme

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

class SmsService{

    fun sendSms(number: String, msg: String, context: Context?){
        val sms : SmsManager = SmsManager.getDefault()
        Log.d("SMS BODY", msg + " | number " + number)
        sms.sendTextMessage(
            number,
            null,
            msg,
            null,
            null
        )
    }
}