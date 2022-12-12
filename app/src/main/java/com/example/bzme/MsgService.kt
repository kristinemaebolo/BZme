package com.example.bzme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.example.bzme.DB.DBHelper
import com.example.bzme.Model.Notification

class MsgService : BroadcastReceiver() {
    val pdu_type = "pdus"
    private val TAG: String = MsgService::class.java.getSimpleName()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onReceive(p0: Context?, p1: Intent) {
        var smsService = SmsService()
        var dbHelper = DBHelper(p0)
        if (p1.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle = p1.extras
            var from = ""
            var msgs: Array<SmsMessage?>
            val pdus = bundle?.get(pdu_type) as Array<Any>?
            if (pdus != null) {
                msgs = arrayOfNulls(pdus.size)
                for (i in msgs.indices) {
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    from = msgs[i]?.originatingAddress.toString()


                    // get ongoing activities
                    var result = dbHelper.hasOnGoingActivity()
                    var message = ""
                    for (i in 0..result.size - 1) {
                        message = result.get(i).reply
                        // Auto reply to sender
                        smsService.sendSms(from, message, p0)
                        // Show Notification
                        if (p0 != null) {
                            popNotification("sms", from, p0)
                        }
                    }
                }
            }
        }

        var state = p1.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            var caller = p1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var result = dbHelper.hasOnGoingActivity()
            var message = ""

            for (i in 0..result.size - 1) {
                message = result.get(i).reply
                smsService.sendSms(caller.toString(), message, p0)

                // Show Notification
                if (p0 != null) {
                    popNotification("call",caller.toString(),p0)
                }
            }
        }
    }

    private fun popNotification(type: String, from: String, p0: Context){
        val notification = p0?.let { Notification(it) }
        var msg = "You have unread message from"
        if(type == "call"){
            msg = "You missed a call from"
        }

        //3rd parameter kay timestamp para unique jd cya
        notification?.sendNotiication(msg,
            from,
            System.currentTimeMillis().toInt())
    }
}