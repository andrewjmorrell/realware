package com.pivot.pivot360.pivoteye

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.moxtra.sdk.ChatClient
import com.pivot.pivot360.content.GraphQLHandler
import com.pivot.pivot360.pivoteye.asset.AssetActivity
import com.pivot.pivot360.pivoteye.chat.MeetActivity
import com.pivot.pivot360.pivoteye.event.EventActivity
import com.pivot.pivot360.pivoteye.task.TaskExecuteActivity
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

/**
 * Base activity
 *
 * @constructor Create empty Base activity
 */
public open class BaseActivity : AppCompatActivity() {
    private var mActionProgressItem: MenuItem? = null
    private var mIsLoading = false

    public val ActionBtnKeyCode = 500

    private val DOCUMENT_REQUEST_CODE = 1890

    private var identity: String? = null

    val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GraphQLHandler.apiBaseUrl = resources.getString(R.string.Api_Url_GraphQL)
        Constants.BASE_URL = resources.getString(R.string.Api_Url)
    }

    override fun onStart() {
        super.onStart()

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
    }

    /**
     * Set loading
     *
     * @param isLoading
     */
    fun setLoading(isLoading: Boolean) {
        mIsLoading = isLoading
        if (mActionProgressItem != null) {
            mActionProgressItem!!.isVisible = mIsLoading
        }
    }

    /**
     * Show toast
     *
     * @param message
     */
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Hide keyboard
     *
     * @param activity
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.showSoftInput(view, 0)
    }

    data class InputMessage(val type: String, val id: String, val token: String, val uniqueid: String)

    inner class AcceptThread : Thread() {

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
                    Log.e("TAG", "!!!!!!Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    BluetoothServer(this@BaseActivity, socket).start()
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

    public open fun onMessageReceived(item: String) {

        var gson = Gson()
        var userInput = gson?.fromJson(item, InputMessage::class.java)

        when (userInput.type) {
            "attachment" -> showAttachment(userInput.id)
            "event" -> showEvent(userInput)
            "meet" -> showMeet(userInput)
            "asset" -> showAsset(userInput)
            "task" -> showTask(userInput)
            "login" -> login(userInput)
        }
    }

    public fun login(userInput: InputMessage) {
        val intent = Intent(this, MyWorkActivity::class.java)
            .putExtra(Constants.TOKEN, userInput.token)
            .putExtra(Constants.UNIQUEID, userInput.uniqueid)
        startActivity(intent)
    }

    fun showAsset(userInput: InputMessage) {
        val intent = Intent(this, AssetActivity::class.java)
            .putExtra(Constants.IDENTITY, userInput.id)
            .putExtra(Constants.TOKEN, userInput.token)
            .putExtra(Constants.UNIQUEID, userInput.uniqueid)
        startActivity(intent)
    }

    fun showTask(userInput: InputMessage) {
        val intent = Intent(this, TaskExecuteActivity::class.java)
            .putExtra(Constants.IDENTITY, userInput.id)
            .putExtra(Constants.TOKEN, userInput.token)
            .putExtra(Constants.UNIQUEID, userInput.uniqueid)
        startActivity(intent)
    }

    fun showEvent(userInput: InputMessage) {
        val intent = Intent(this, EventActivity::class.java)
            .putExtra(Constants.IDENTITY, userInput.id)
            .putExtra(Constants.TOKEN, userInput.token)
            .putExtra(Constants.UNIQUEID, userInput.uniqueid)
        startActivity(intent)
    }

    fun showMeet(userInput: InputMessage) {

        runOnUiThread {
            ChatClient.initialize(application)
            ChatClient.setupDomain(Constants.BASE_DOMAIN, null, null, null)

            MeetActivity.joinMeetNotification(this, userInput.id, userInput.uniqueid)
        }
    }

    fun showAttachment(link: String)  = runBlocking{

        val url = URL(link)
        var fname = url.file

        if (fname.endsWith("jpe")) {
            fname += "g"
        }

        val file = copyFromUrlToExternalAsync(this@BaseActivity, link,
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
            Log.e("TAG", "Audio")
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
                        Log.i("TAG", "Reading")
                        inputStream.read(bytes, 0, available)
                        val text = String(bytes)
                        Log.e(TAG, "!!!!!!Message received")
                        Log.e(TAG, "!!!!!" + text)
                        identity = text
                        onMessageReceived(text)
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

    companion object {

        private const val TAG = "BaseActivity"
    }
}