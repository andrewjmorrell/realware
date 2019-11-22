package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import com.moxtra.sdk.ChatClient
import com.pivot.pivot360.content.graphql.AccountQuery
import com.pivot.pivot360.content.listeners.GetAccountResponseListener
import com.pivot.pivot360.content.listeners.GetUserResponseListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R

class MainActivity : BaseActivity(), GetUserResponseListener, GetAccountResponseListener {


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
            identity = intent.getStringExtra("identity")
                //"248ae20bdc17a394df11e7a0766a60df"
            mToken = intent.getStringExtra("token")
                //"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNTY5NDUzOTE1LCJuYmYiOjE1Njk0NTM5MTUsImp0aSI6IjczOGM2YmQ2LWY4MGItNGMyYi04OGY4LTY3NjJjM2UyZDdiOCIsImV4cCI6MTU4NTAwNTkxNSwiaWRlbnRpdHkiOiJhbmRyZXcubW9ycmVsbEBwaXZvdC5jb20ifQ.8jmZxIfLXIBN-KYXcUKw5FQ1vTG3Wcjlohd_Yi6BBoU"//
            callUserDetailApi()
        }

    }

    private fun callUserDetailApi() {
        GraphQlApiHandler.instance
            .getUser(AccountQuery.builder()
                .token(mToken!!)
                .build(), this)
    }

    private fun getAccountDetails() {
        GraphQlApiHandler.instance
            .getAccount(
                AccountQuery.builder()
                    .token(mToken!!).build(), this)
    }

    private fun getEvent() {
//        GraphQlApiHandler.instance.getEvent(
//            GetEventQuery.builder()
//                .token(mToken!!)
//                .id(identity).build(), this)
    }

    private fun finishInMainThread() {
        mHandler.post { finish() }
    }

    override fun AccountField(response: AccountQuery.AsAccountField?) {
        PreferenceUtil.saveUserUniqueIdentity(this@MainActivity, response?.identity()!!)
        val launchIntent = Intent(this, MenuActivity::class.java)
        launchIntent.putExtra("token", mToken)
        launchIntent.putExtra("identity", identity)
        startActivity(launchIntent)
        finish()
    }

    override fun AccountResponse(response: AccountQuery.AsAccountField?) {
        PreferenceUtil.saveUserUniqueIdentity(this@MainActivity, response?.identity()!!)
//        ChatClient.linkWithUniqueId(response?.identity()!!, CLIENT_ID, CLIENT_SECRET, ORG_ID, object :
//            ApiCallback<ChatClientDelegate> {
//            override fun onCompleted(ccd: ChatClientDelegate) {
//                Log.i(TAG, "Linked to Moxtra account successfully.")
//
//            }
//
//            override fun onError(errorCode: Int, errorMsg: String) {
//                //  Toast.makeText(mContext, getString(R.string.failed_to_link_to_moxtra_account), Toast.LENGTH_LONG).show()
//                Log.e(TAG, "Failed to link to Moxtra account, errorCode=$errorCode, errorMsg=$errorMsg")
//                //  showProgress(false)
//            }
//        })
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show() }
    }

}
