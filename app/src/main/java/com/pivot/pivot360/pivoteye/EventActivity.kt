package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.moxtra.sdk.chat.controller.ChatConfig
import com.moxtra.sdk.chat.controller.ChatController
import com.moxtra.sdk.chat.model.Chat
import com.moxtra.sdk.client.ChatClientDelegate
import com.pivot.pivot360.pivoteye.GlassGestureDetector.*

//import com.pivot.pivot360.pivoteye.MeetActivity.Companion.startMeet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.moxtra.sdk.ChatClient
import com.moxtra.sdk.chat.model.ChatMember
import com.moxtra.sdk.chat.repo.ChatRepo
import com.moxtra.sdk.common.ApiCallback
import com.moxtra.sdk.common.BaseRepo
import com.moxtra.sdk.common.model.User
import com.moxtra.sdk.meet.controller.MeetSessionController
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.model.MeetSession
import com.moxtra.sdk.meet.repo.MeetRepo
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.listeners.EventResponseListener
import com.pivot.pivot360.content.listeners.OnAttachmentDataClickListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.fragment_attachment.*
import java.util.ArrayList


class EventActivity : BaseActivity(), EventResponseListener,
    OnItemClickListener {

    private var mToken: String? = null
    lateinit var mAdapter: EventsAttachmentAdapter

    lateinit var identity: String


    private var glassGestureDetector: GlassGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_event)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)


        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            mToken = intent.getStringExtra("token")
            GraphQlApiHandler.instance.getEvent(EventQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun OnEventsField(response: EventQuery.AsEventField?) {

        runOnUiThread {
            val adapter = EventsAttachmentAdapter(this, response?.attachments(), this)
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

    override fun onItemClick(item: String) {
        startActivity(Intent(this, WebActivity::class.java).putExtra("link", item))
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}

