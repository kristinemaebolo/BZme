package com.example.bzme

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class CallService : CallScreeningService() {
    override fun onScreenCall(p0: Call.Details) {
        TODO("Not yet implemented")
        val phoneNumber = p0.handle.schemeSpecificPart
        Log.d("caller", p0.callerDisplayName)
    }

}