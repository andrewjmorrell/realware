package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventsByAssetQuery
import com.pivot.pivot360.content.graphql.EventsByUserQuery
import com.pivot.pivot360.content.listeners.EventsByUserResponseListener
import com.pivot.pivot360.content.listeners.GetEventsByAssets
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_events.*


class EventsByAssetActivity : BaseActivity(), GetEventsByAssets,
    OnItemClickListener {

    private var mToken: String? = null
    lateinit var mAdapter: EventAttachmentAdapter


    private var glassGestureDetector: GlassGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_events)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)


        if (intent != null) {
            mToken = PreferenceUtil.getToken(this)
            val identity = intent.getStringExtra("identity")
            GraphQlApiHandler.instance.getEventByAssets(EventsByAssetQuery.builder()
                .token(mToken!!)
                .id(identity)
                .status("active").build(), this)
        } else {
            Toast.makeText(this, "Events not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onEventsByAssets(response: EventsByAssetQuery.AsEventResults?) {

        runOnUiThread {
            val adapter = EventsByAssetAdapter(this, ArrayList(response?.events()!!), this)
            if (mRecyclerViewAttachment != null) {
                mRecyclerViewAttachment.adapter = adapter
                if (response?.events()?.size == 0) {
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
        val intent = Intent(this, MainActivity::class.java)
        var extras = hashMapOf(Pair("identity", item), Pair("token", mToken))
        for (entry in extras.entries) {
            intent.putExtra(entry.key, entry.value)
        }
        startActivity(intent)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
