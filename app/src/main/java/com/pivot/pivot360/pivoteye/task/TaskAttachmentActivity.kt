package com.pivot.pivot360.pivoteye.task

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.BaseActivity
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
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

    private var identity: String? = null
    private var token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_events)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)


        if (intent != null) {
            identity = intent.getStringExtra(Constants.IDENTITY)
            token = intent.getStringExtra(Constants.TOKEN)
            GraphQlApiHandler.instance
                .getData<UserTaskQuery, GenericListener<Any>>(
                    UserTaskQuery.builder()
                        .token(token!!)
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

        showAttachment(item)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}

