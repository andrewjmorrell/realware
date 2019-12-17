package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.*
import com.pivot.pivot360.content.listeners.*
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), GenericListener<Any>,
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
            val list = arrayListOf<String>("andrew.morrell@pivot.com", "neha.mohta@pivot.com")
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

    fun onAuthField(response: AuthMutation.AsAuthField?) {
        PreferenceUtil.saveAccessToken(this, response?.accessToken())
        callUserDetailApi()
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is AccountQuery.Data -> {
                var account = response.account()
                account.let {
                    when (it) {
                        is AccountQuery.AsAccountField -> {
                            PreferenceUtil.saveUserUniqueIdentity(this@LoginActivity, it.identity()!!)
                            //val intent = Intent(this, AssetsActivity::class.java)
                            //val intent = Intent(this, MyWorkActivity::class.java)
                            val intent = Intent(this, EventsActivity::class.java)
                            startActivity(intent)
                        }
                        is AccountQuery.AsAuthInfoField -> {
                            showToast(it.message()!!)
                        }
                        is AccountQuery.Account -> {

                        }
                    }
                }
            }
            is AuthMutation.Data -> {
                response.auth()?.result().let {
                    when (it) {
                        is AuthMutation.AsAuthField -> {
                            onAuthField(it)
                        }
                        is AuthMutation.AsResponseMessageField -> {
                            onResponseMessageField(it.message()!!)
                        }
                    }
                }
            }
        }
    }

    private fun callUserDetailApi() {

        GraphQlApiHandler.instance
            .getData<AccountQuery, GenericListener<Any>>(AccountQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .build(), this)

    }

    override fun onItemClick(item: String) {
        GraphQlApiHandler.instance.postData<AuthMutation,
                GenericListener<Any>>(AuthMutation.builder().email(item).password("123123").build(), this)
    }

    companion object {

        private const val TAG = "DEMO_EventActivity"
    }

}
