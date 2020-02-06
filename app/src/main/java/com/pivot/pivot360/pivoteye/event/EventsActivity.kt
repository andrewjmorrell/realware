package com.pivot.pivot360.pivoteye.event

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.EventsByUserQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.BaseActivity
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_events.*


class EventsActivity : BaseActivity(), GenericListener<Any>,
    OnItemClickListener {

    private lateinit var mToken: String
    private lateinit var mUniqueId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_events)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewAttachment)

        if (intent != null) {
            mToken = intent.getStringExtra("token")
            mUniqueId = intent.getStringExtra("uniqueid")
        }
        GraphQlApiHandler.instance
            .getData<EventsByUserQuery, GenericListener<Any>>(EventsByUserQuery.builder()
                .token(mToken).build(), this)

        setLoading(false)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is EventsByUserQuery.Data -> {
                var eventsByUser = response.eventsByUser()
                eventsByUser.let {
                    when (it) {
                        is EventsByUserQuery.AsEventResults -> {
                            onEventResults(it)
                        }

                        is EventsByUserQuery.AsResponseMessageField -> {
                            onResponseMessageField(it.message())
                        }
                        is EventsByUserQuery.AsAuthInfoField -> {
                            onAuthInfoField(it.message())
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventsActivity, message, Toast.LENGTH_SHORT).show() }
    }

    fun onEventResults(response: EventsByUserQuery.AsEventResults?) {

        runOnUiThread {
            val adapter = EventsAdapter(
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
        val intent = Intent(this, EventActivity::class.java)
            .putExtra(Constants.IDENTITY, item)
            .putExtra(Constants.TOKEN, mToken)
        startActivity(intent)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
