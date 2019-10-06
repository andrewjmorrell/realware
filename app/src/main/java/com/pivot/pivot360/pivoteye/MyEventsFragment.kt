package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pivot.pivot360.content.graphql.EventsByUserQuery

import kotlinx.android.synthetic.main.fragment_events_by_user.*
import kotlin.math.ceil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSnapHelper
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R


class MyEventsFragment : Fragment(), AdapterView.OnItemClickListener {
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun onItemClick(item: String?) {
//        startActivity(Intent(activity, EventActivity::class.java)
//                .putExtra("identity", item))
//    }

//    override fun onEventResults(response: EventsByUserQuery.AsEventResults?) {
//        activity?.runOnUiThread {
//            totalPages = response?.total()!!
//            (rvEventsByUser.adapter as EventsByUserAdapter).add(ArrayList(response.events()!!))
//            totalPages = ceil((response.total()!! / 20).toDouble()).toInt()
//            if (currentPage == totalPages) {
//                isLastPage = true
//            }
//        }
//    }
//
//    override fun onError(message: String?) {
//        activity?.runOnUiThread {
//            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onAuthInfoField(message: String?) {
//        activity?.runOnUiThread {
//            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onResponseMessageField(message: String?) {
//        activity?.runOnUiThread {
//            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
//        }
//    }

    // Index from which pagination should start (0 is 1st page in our case)
    private val pageStart = 1

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private val isLoading = false

    // If current page is the last page (Pagination will stop after this page load)
    private var isLastPage = false

    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private var totalPages = 0
    // indicates the current page which Pagination is fetching.
    private var currentPage = pageStart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_events_by_user, container, false)
        // activity.supportActionBar?.title = getString(R.string.events_by_user)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvEventsByUser.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = EventsByUserAdapter(activity!!, ArrayList(), this@MyEventsFragment)

        }

        val snapHelper = object : LinearSnapHelper() {
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager?,
                velocityX: Int,
                velocityY: Int
            ): Int {
                val centerView =
                    findSnapView(layoutManager!!) ?: return RecyclerView.NO_POSITION

                val position = layoutManager.getPosition(centerView)
                var targetPosition = -1
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1
                    } else {
                        targetPosition = position + 1
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1
                    } else {
                        targetPosition = position + 1
                    }
                }

                val firstItem = 0
                val lastItem = layoutManager.itemCount - 1
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem))
                return targetPosition
            }
        }

        snapHelper.attachToRecyclerView(rvEventsByUser)

        rvEventsByUser.addOnScrollListener(object : PaginationScrollListener(rvEventsByUser.layoutManager as LinearLayoutManager?) {
            override fun loadMoreItems() {
                //  isLoading = true
                //Increment page index to load the next one
                currentPage += 1
                callApi(currentPage)
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }


        })
        callApi(currentPage)
    }


    private fun callApi(currentPage: Int) {
//        GraphQlApiHandler.instance
//                .getEventsByUser(EventsByUserQuery.builder()
//                        .count(false)
//                        .page(currentPage)
//                        .token(PreferenceUtil.getToken(activity!!)!!)
//                        .build(),
//                        this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyAssetsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = MyEventsFragment()

    }
}
