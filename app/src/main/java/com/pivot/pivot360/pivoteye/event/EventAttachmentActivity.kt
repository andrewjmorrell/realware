package com.pivot.pivot360.pivoteye.event

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.graphql.EventsQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.BaseActivity
import com.pivot.pivot360.pivoteye.Constants
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




class EventAttachmentActivity : BaseActivity(), GenericListener<Any>,
    OnItemClickListener {

    private var mToken: String? = null
    lateinit var mAdapter: EventAttachmentAdapter

    private var identity: String? = null

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
            mToken = intent.getStringExtra(Constants.TOKEN)
            GraphQlApiHandler.instance.getData<EventQuery, GenericListener<Any>>(EventQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is EventQuery.Data -> {

                response.event().let {
                    when (it) {
                        is EventQuery.AsEventField -> {
                            onEventsField(it)
                        }
                        is EventsQuery.AsResponseMessageField -> {
                            onResponseMessageField(it.message())
                        }
                        is EventsQuery.AsAuthInfoField -> {
                            onAuthInfoField(it.message())
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    fun onEventsField(response: EventQuery.AsEventField?) {

        runOnUiThread {
            val adapter =
                EventAttachmentAdapter(
                    this,
                    response?.attachments(),
                    this
                )
            if (mRecyclerViewAttachment != null) {
                mRecyclerViewAttachment.adapter = adapter
                if (response?.attachments()?.size == 0) {
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

