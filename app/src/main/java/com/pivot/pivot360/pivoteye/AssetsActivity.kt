package com.pivot.pivot360.pivoteye


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.pivot.pivot360.content.graphql.AssetsQuery
import com.pivot.pivot360.content.listeners.GetAssestsResponseListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_assets.*


class AssetsActivity : AppCompatActivity(), GetAssestsResponseListener {
//    override fun onGetAssets(response: AssetsQuery.AsAssetResults) {
//        when (response) {
//            is AssetsQuery.Data -> {
//                var event = response.assets()
//                event.let {
//                    when (it) {
//                        is AssetsQuery.AsAssetResults -> {
//                            onGetAssets(it)
//                        }
//                        is AssetsQuery.AsResponseMessageField -> {
//                            onResponseMessageField(it.message()!!)
//                        }
//                        is AssetsQuery.AsAuthInfoField -> {
//                            onAuthInfoField(it.message()!!)
//                        }
//                    }
//                }
//            }
//        }
//    }

    private var mActionProgressItem: MenuItem? = null
    private var mIsLoading = false

    private var mToken: String? = null

    override fun onError(message: String?) {

    }

    override fun onAuthInfoField(message: String?) {

    }

    override fun onGetAssets(response: AssetsQuery.AsAssetResults?) {
        this.runOnUiThread {
            val list = response?.assets()
            var mAdapter = AssetsAdapter(list!!, this@AssetsActivity)
            mRecyclerView.adapter = mAdapter
        }
    }

    override fun onResponseMessageField(message: String?) {

    }

    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//    private var mToken: String? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_assets)

        val snapHelper = PagerSnapHelper()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        snapHelper.attachToRecyclerView(mRecyclerView)

        mToken = PreferenceUtil.getToken(this)
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
//        GraphQlApiHandler.instance
//                .getAssets(AssetsQuery.builder()
//                        .token(mToken!!)
//                        .build(),
//                        this)
        GraphQlApiHandler.instance
                .getAssets(AssetsQuery.builder()
                        .token(mToken!!)
                        .build(),
                        this)
    }
}
