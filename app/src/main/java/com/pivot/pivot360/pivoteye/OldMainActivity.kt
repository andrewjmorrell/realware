package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import com.moxtra.sdk.ChatClient
import com.pivot.pivot360.content.graphql.AccountQuery
import com.pivot.pivot360.content.graphql.EntityByChatIdQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.event.EventActivity
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R

class OldMainActivity : BaseActivity(), GenericListener<Any> {


    lateinit var progressText: TextView
    private val mHandler = Handler()

    lateinit var mToken: String

    lateinit var identity: String

    private val BASE_DOMAIN = "sandbox.moxtra.com"


    val TAG = "360"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_main)

        progressText = findViewById(R.id.progress_text)

        ChatClient.setupDomain(BASE_DOMAIN, null, null, null)

        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = //intent.getStringExtra("identity")
                "248ae20bdc17a394df11e7a0766a60df"
            mToken = intent.getStringExtra("token")
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNTY5NDUzOTE1LCJuYmYiOjE1Njk0NTM5MTUsImp0aSI6IjczOGM2YmQ2LWY4MGItNGMyYi04OGY4LTY3NjJjM2UyZDdiOCIsImV4cCI6MTU4NTAwNTkxNSwiaWRlbnRpdHkiOiJhbmRyZXcubW9ycmVsbEBwaXZvdC5jb20ifQ.8jmZxIfLXIBN-KYXcUKw5FQ1vTG3Wcjlohd_Yi6BBoU"//
            callUserDetailApi()
        }

    }

    private fun callUserDetailApi() {
        GraphQlApiHandler.instance
            .getData<AccountQuery, GenericListener<Any>>(AccountQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .build(), this)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is AccountQuery.Data -> {
                var account = response.account()
                account.let {
                    when (it) {
                        is AccountQuery.AsAccountField -> {
                            accountResponse(it)
                        }
                        is AccountQuery.AsAuthInfoField -> {
                            showToast(it.message()!!)
                        }
                        is AccountQuery.Account -> {

                        }
                    }
                }
            }
            is AccountQuery.Data -> {
                var event = response.account()
                event.let {
                    when (it) {
                        is AccountQuery.AsAccountField -> {
                            accountField(it)
                        }
                        is EntityByChatIdQuery.AsAuthInfoField -> {
                            showToast(it.message()!!)
                        }
                        is EntityByChatIdQuery.AsResponseMessageField -> {

                        }
                    }
                }
            }
        }
    }

    fun accountField(response: AccountQuery.AsAccountField?) {
        PreferenceUtil.saveUserUniqueIdentity(this@OldMainActivity, response?.identity()!!)
        val launchIntent = Intent(this, EventActivity::class.java)
        launchIntent.putExtra("token", mToken)
        launchIntent.putExtra("identity", identity)
        startActivity(launchIntent)
        finish()
    }

    fun accountResponse(response: AccountQuery.AsAccountField?) {
        PreferenceUtil.saveUserUniqueIdentity(this@OldMainActivity, response?.identity()!!)
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@OldMainActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@OldMainActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@OldMainActivity, message, Toast.LENGTH_SHORT).show() }
    }

}
