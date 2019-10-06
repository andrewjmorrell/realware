package com.pivot.pivot360.pivoteye

import android.app.Application
import com.moxtra.sdk.ChatClient
import android.os.StrictMode



class PivotApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ChatClient.initialize(this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

    }
}