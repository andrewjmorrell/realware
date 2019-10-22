package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast

//import com.pivot.pivot360.pivoteye.MeetActivity.Companion.startMeet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.listeners.EventResponseListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.fragment_attachment.*


class EventAttachmentActivity : BaseActivity(), EventResponseListener,
    OnItemClickListener {

    private var mToken: String? = null
    lateinit var mAdapter: EventAttachmentAdapter

    lateinit var identity: String


    private var glassGestureDetector: GlassGestureDetector? = null

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
            mToken = intent.getStringExtra("token")
            GraphQlApiHandler.instance.getEvent(EventQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@EventAttachmentActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun OnEventsField(response: EventQuery.AsEventField?) {

        runOnUiThread {
            val adapter = EventAttachmentAdapter(this, response?.attachments(), this)
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

