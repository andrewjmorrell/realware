package com.pivot.pivot360.pivoteye.event

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventsByAssetQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_events.*


class EventsByAssetActivity : BaseActivity(), GenericListener<Any>,
    OnItemClickListener {

    private var mToken: String? = null
    private var identity: String? = null
    lateinit var mAdapter: EventAttachmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_events)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)


        if (intent != null) {
            mToken = intent.getStringExtra(Constants.TOKEN)
            identity = intent.getStringExtra(Constants.IDENTITY)
            GraphQlApiHandler.instance
                .getData<EventsByAssetQuery, GenericListener<Any>>(EventsByAssetQuery.builder()
                    .token(mToken!!)
                    .id(identity!!)
                    .status("active")
                    .page(1)
                    .build(),
                    this)
        } else {
            Toast.makeText(this, "Events not found.", Toast.LENGTH_SHORT).show()
        }

        setLoading(false)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is EventsByAssetQuery.Data -> {
                var eventsByAsset = response.eventsByAsset()
                eventsByAsset.let {
                    when (it) {
                        is EventsByAssetQuery.AsEventResults -> {
                            onEventsByAssets(it)
                        }

                        is EventsByAssetQuery.AsResponseMessageField -> {
                            onResponseMessageField(it.message())
                        }
                        is EventsByAssetQuery.AsAuthInfoField -> {
                            onAuthInfoField(it.message())
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsByAssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    fun onEventsByAssets(response: EventsByAssetQuery.AsEventResults?) {

        runOnUiThread {
            val adapter = EventsByAssetAdapter(
                this,
                ArrayList(response?.events()!!),
                this
            )
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
        startActivity(Intent(this, EventActivity::class.java)
            .putExtra(Constants.IDENTITY, item)
            .putExtra(Constants.TOKEN, mToken))
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
