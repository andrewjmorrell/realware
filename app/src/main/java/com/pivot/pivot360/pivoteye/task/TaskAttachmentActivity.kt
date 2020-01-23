package com.pivot.pivot360.pivoteye.task

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.graphql.EventsQuery
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.BaseActivity
import com.pivot.pivot360.pivoteye.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_attachment.*
import com.pivot.pivot360.pivotglass.R
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL




class TaskAttachmentActivity : BaseActivity(), GenericListener<Any>,
    OnItemClickListener {

    private var mToken: String? = null
    lateinit var mAdapter: TaskAttachmentAdapter

    lateinit var identity: String

    private val DOCUMENT_REQUEST_CODE = 1890

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_events)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)


        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            GraphQlApiHandler.instance
                .getData<UserTaskQuery, GenericListener<Any>>(
                    UserTaskQuery.builder()
                        .token(PreferenceUtil.getToken(this)!!)
                        .id(identity)
                        .build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is UserTaskQuery.Data -> {
                response.userTask().let {
                    when (it) {
                        is UserTaskQuery.AsUserTaskField -> {
                            onUserTaskResults(it.task()?.attachments()!!)
                        }

                        is UserTaskQuery.AsResponseMessageField -> {
                            it.message()?.let { it1 -> showToast(it1) }
                        }
                        is UserTaskQuery.Task -> {

                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskAttachmentActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    private fun onUserTaskResults(attachment: MutableList<UserTaskQuery.Attachment1>) {

        runOnUiThread {
            val adapter = TaskAttachmentAdapter(this, attachment, this)
            if (mRecyclerViewAttachment != null) {
                mRecyclerViewAttachment.adapter = adapter
                if (attachment.size == 0) {
                    emptyView.visibility = View.VISIBLE
                    mRecyclerViewAttachment.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    mRecyclerViewAttachment.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onItemClick(item: String) = runBlocking{

        val url = URL(item)
        var fname = url.file

        if (fname.endsWith("jpe")) {
            fname += "g"
        }

        val file = copyFromUrlToExternalAsync(this@TaskAttachmentActivity, item,
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

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}

