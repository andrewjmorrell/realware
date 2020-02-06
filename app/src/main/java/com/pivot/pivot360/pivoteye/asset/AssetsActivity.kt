package com.pivot.pivot360.pivoteye.asset


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.AssetsQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_assets.*


class AssetsActivity : AppCompatActivity(), GenericListener<Any>, OnItemClickListener {

    private var mActionProgressItem: MenuItem? = null
    private var mIsLoading = false

    private lateinit var mToken: String
    private lateinit var mUniqueId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_assets)

        if (intent != null) {
            mToken = intent.getStringExtra("token")
            mUniqueId = intent.getStringExtra("uniqueid")
        } else {
            Toast.makeText(this, "Error getting user token", Toast.LENGTH_SHORT).show()
        }

        val snapHelper = PagerSnapHelper()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerView)

        callApi()

        setLoading(false)
    }

    fun setLoading(isLoading: Boolean) {
        mIsLoading = isLoading
        if (mActionProgressItem != null) {
            mActionProgressItem!!.isVisible = mIsLoading
        }
    }

    private fun callApi() {
        GraphQlApiHandler.instance
            .getData<AssetsQuery, GenericListener<Any>>(AssetsQuery.builder()
                .token(mToken)
                .build(),
                this)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is AssetsQuery.Data -> {

                response.assets().let {
                    when (it) {
                        is AssetsQuery.AsAssetResults -> {
                            onGetAssets(it)
                        }
                        is AssetsQuery.AsResponseMessageField -> {
                            onResponseMessageField(it.message()!!)
                        }
                        is AssetsQuery.AsAuthInfoField -> {
                            onAuthInfoField(it.message()!!)
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {

    }

    override fun onAuthInfoField(message: String?) {

    }

    fun onGetAssets(response: AssetsQuery.AsAssetResults?) {
        this.runOnUiThread {
            val list = response?.assets()
            var mAdapter = AssetsAdapter(
                this@AssetsActivity,
                list!!,
                this@AssetsActivity
            )
            mRecyclerView.adapter = mAdapter
        }
    }

    override fun onResponseMessageField(message: String?) {

    }

    override fun onItemClick(item: String?) {
        startActivity(
            Intent(this, AssetActivity::class.java)
                .putExtra(Constants.IDENTITY, item)
                .putExtra(Constants.TOKEN, mToken))
    }
}
