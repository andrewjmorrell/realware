package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.AuthMutation
import com.pivot.pivot360.content.graphql.EventsByUserQuery
import com.pivot.pivot360.content.listeners.*
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), LoginResponseListener,
    OnItemClickListener {

    private var mToken: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtil.removeAccessToken(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_login)

        val snapHelper = PagerSnapHelper()
        mRecyclerViewUsers.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerViewUsers)

        runOnUiThread {
            val list = arrayListOf<String>("andrew.morrell@pivot.com")
            val adapter = LoginAdapter(this, list, this)
            if (mRecyclerViewUsers != null) {
                mRecyclerViewUsers.adapter = adapter
                if (list.size == 0) {
                    emptyView.visibility = View.VISIBLE
                    mRecyclerViewUsers.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    mRecyclerViewUsers.visibility = View.VISIBLE
                }
            }
        }

        setLoading(false)
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

    override fun onAuthField(response: AuthMutation.AsAuthField?) {
        PreferenceUtil.saveAccessToken(this, response?.accessToken())
        //val intent = Intent(this, AssetsActivity::class.java)
        val intent = Intent(this, EventsActivity::class.java)
        var extras = hashMapOf(Pair("token", response?.accessToken()))
        for (entry in extras.entries) {
            intent.putExtra(entry.key, entry.value)
        }
        startActivity(intent)
    }

    override fun onItemClick(item: String) {
        GraphQlApiHandler.instance.makeLogin(AuthMutation.builder().email(item).password("123123").build(), this)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
