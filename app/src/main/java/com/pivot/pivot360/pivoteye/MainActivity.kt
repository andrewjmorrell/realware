package com.pivot.pivot360.pivoteye

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.moxtra.sdk.ChatClient
import com.moxtra.sdk.chat.controller.ChatController
import com.moxtra.sdk.chat.model.Chat
import com.moxtra.sdk.chat.repo.ChatRepo
import com.moxtra.sdk.client.ChatClientDelegate
import com.moxtra.sdk.meet.controller.MeetSessionController
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.repo.MeetRepo
import com.pivot.pivot360.content.graphql.*
import com.pivot.pivot360.content.listeners.*
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.Constants.BASE_DOMAIN
import com.pivot.pivot360.pivoteye.asset.AssetActivity
import com.pivot.pivot360.pivoteye.chat.MeetActivity
import com.pivot.pivot360.pivoteye.event.EventActivity
import com.pivot.pivot360.pivoteye.task.TaskExecuteActivity
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*


class MainActivity : BaseActivity() {

    private var acceptThread: AcceptThread? = null

    private lateinit var messageView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_login)

        messageView = findViewById(R.id.message)
    }

    override fun onStart() {
        super.onStart()

        message.text = "Waiting for a request from the mobile device."

        //setLoading(false)

        acceptThread = AcceptThread()

        acceptThread?.start()

        //onItemClick("andrew.morrell@pivot.com")
    }

    override fun onStop() {
        super.onStop()

        acceptThread?.cancel()
        acceptThread = null
    }

    override fun onMessageReceived(item: String) {
        runOnUiThread {
            message?.text = "Message Received.  Please Wait..."
        }

        super.onMessageReceived(item)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
