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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.moxtra.sdk.ChatClient
import com.moxtra.sdk.chat.controller.ChatController
import com.moxtra.sdk.chat.model.Chat
import com.moxtra.sdk.chat.model.ChatMember
import com.moxtra.sdk.chat.repo.ChatRepo
import com.moxtra.sdk.client.ChatClientDelegate
import com.moxtra.sdk.common.ApiCallback
import com.moxtra.sdk.common.BaseRepo
import com.moxtra.sdk.common.model.User
import com.moxtra.sdk.meet.controller.MeetSessionController
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.repo.MeetRepo
import com.pivot.pivot360.content.graphql.*
import com.pivot.pivot360.content.listeners.*
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.Constants.BASE_DOMAIN
import com.pivot.pivot360.pivoteye.Constants.CLIENT_ID
import com.pivot.pivot360.pivoteye.Constants.CLIENT_SECRET
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


class LoginActivity : BaseActivity(), GenericListener<Any>,
    OnItemClickListener {

    lateinit var mChatClientDelegate: ChatClientDelegate
    lateinit var mChatController: ChatController
    lateinit var mMeetSessionController: MeetSessionController
    lateinit var mChatRepo: ChatRepo
    lateinit var mChat: Chat
    lateinit var mMeetRepo: MeetRepo
    lateinit var mMeet: Meet

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var acceptThread: AcceptThread? = null

    private var identity: String? = null

    private lateinit var messageView: TextView

    private val DOCUMENT_REQUEST_CODE = 1890


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtil.removeAccessToken(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_login)

        messageView = findViewById(R.id.message)
    }

    override fun onStart() {
        super.onStart()

        message.text = "Waiting for connection"

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1001)
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            Log.e("TAG", "!!!!!!!Device="+deviceName+" mac="+deviceHardwareAddress)
        }

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

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show() }
    }

    fun onAuthField(response: AuthMutation.AsAuthField?) {
        PreferenceUtil.saveAccessToken(this, response?.accessToken())
        callUserDetailApi()
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is AccountQuery.Data -> {
                var account = response.account()
                account.let {
                    when (it) {
                        is AccountQuery.AsAccountField -> {
                            PreferenceUtil.saveUserUniqueIdentity(this@LoginActivity, it.identity()!!)
                            //val intent = Intent(this, AssetsActivity::class.java)
                            val intent = Intent(this, EventActivity::class.java)
                            var extras = hashMapOf(Pair("identity", identity), Pair("token", PreferenceUtil.getToken(this)))
                            for (entry in extras.entries) {
                                intent.putExtra(entry.key, entry.value)
                            }
                            //val intent = Intent(this, EventsActivity::class.java)
                            startActivity(intent)
                        }
                        is AccountQuery.AsAuthInfoField -> {
                            showToast(it.message()!!)
                        }
                        is AccountQuery.Account -> {

                        }
                    }
                }
            }
            is AuthMutation.Data -> {
                response.auth()?.result().let {
                    when (it) {
                        is AuthMutation.AsAuthField -> {
                            onAuthField(it)
                        }
                        is AuthMutation.AsResponseMessageField -> {
                            onResponseMessageField(it.message()!!)
                        }
                    }
                }
            }
        }
    }

    private fun callUserDetailApi() {

        GraphQlApiHandler.instance
            .getData<AccountQuery, GenericListener<Any>>(AccountQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .build(), this)

    }

    override fun onItemClick(item: String) {
        runOnUiThread {
            message.text = "Connected"
        }

        var gson = Gson()
        var userInput = gson?.fromJson(item, InputMessage::class.java)

        when (userInput.type) {
            "attachment" -> showAttachment(userInput.id)
            "event" -> showEvent(userInput)
            "meet" -> showMeet(userInput)
        }
    }

    private fun showEvent(userInput: InputMessage) {
        val intent = Intent(this, EventActivity::class.java)
            .putExtra("identity", userInput.id)
            .putExtra("token", userInput.token)
            .putExtra("uniqueid", userInput.uniqueid)
        startActivity(intent)
    }

    private fun showMeet(userInput: InputMessage) {

        runOnUiThread {
            ChatClient.initialize(application)
            ChatClient.setupDomain(BASE_DOMAIN, null, null, null)

            MeetActivity.joinMeetNotification(this, userInput.id, userInput.uniqueid)
        }
    }

    private fun showAttachment(link: String)  = runBlocking{

            val url = URL(link)
            var fname = url.file

            if (fname.endsWith("jpe")) {
                fname += "g"
            }

            val file = copyFromUrlToExternalAsync(this@LoginActivity, link,
                fname, "Pivot").await()

            var mimetype = when(file.extension) {
                "jpe" -> "image/jpeg"
                "jpeg"-> "image/jpeg"
                "pdf" -> "application/pdf"
                "mp4" -> "video/mp4"
                "wav" -> "audio/wav"
                else -> "application/pdf"
            }

            if (mimetype == "audio/wav") {

            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)

                intent.setDataAndType(Uri.fromFile(file), mimetype)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                //
                // Optionally can control visual appearance
                //
                intent.putExtra("page", "1") // Open a specific page
                intent.putExtra("zoom", "1") // Open at a specific zoom level

                startActivityForResult(intent, DOCUMENT_REQUEST_CODE)
            }
    }

    private inner class BluetoothServer(private val activity: AppCompatActivity, private val socket: BluetoothSocket): Thread() {
        private val inputStream = this.socket.inputStream
        private val outputStream = this.socket.outputStream

        override fun run() {
            try {
                var shouldLoop = true
                while (shouldLoop) {
                    val available = inputStream.available()
                    if (available > 0) {
                        val bytes = ByteArray(available)
                        Log.i("server", "Reading")
                        inputStream.read(bytes, 0, available)
                        val text = String(bytes)
                        Log.e("server", "!!!!!!Message received")
                        Log.e("server", "!!!!!" + text)
                        identity = text
                        onItemClick(text)
                        if (text.isNotEmpty()) {
                            shouldLoop = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("client", "Cannot read data", e)
            } finally {
                inputStream.close()
                outputStream.close()
                socket.close()
            }
        }
    }

    @Throws(IOException::class)
    fun copyFromUrlToExternalAsync(
        context: Context,
        url: String,
        filename: String,
        destinationFolder: String
    ): Deferred<File> = GlobalScope.async{

        val outputFile = File(context.getExternalFilesDir(destinationFolder), filename)
        if (!outputFile.exists()) {
            val inputStream = BufferedInputStream(URL(url).openStream())

            val outputStream = FileOutputStream(outputFile)

            var bytes: ByteArray = inputStream.readBytes()
            outputStream.write(bytes)

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }

        outputFile
    }

    data class InputMessage(val type: String, val id: String, val token: String, val uniqueid: String)

    val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")

    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("Galaxy Tab A (2016)", uuid)
        }

        override fun run() {
            Log.e(TAG, "!!!!Listening for socket connection")
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "!!!!!!Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    BluetoothServer(this@LoginActivity, socket).start()
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
