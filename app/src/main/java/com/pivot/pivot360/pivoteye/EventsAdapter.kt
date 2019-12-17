package com.pivot.pivot360.pivoteye

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.common.Utilities
import com.pivot.pivot360.content.graphql.EventsByUserQuery
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.active_event_list_item.view.*

class EventsAdapter(context: FragmentActivity, events: ArrayList<EventsByUserQuery.Event>, myListener: OnItemClickListener) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private var listData: ArrayList<EventsByUserQuery.Event> = events
    private var mContext: Context = context
    private var listener: OnItemClickListener = myListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.active_event_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(listData[position]) {
            holder.itemView.apply {
                txtActiveEventTitle.text = assetTitle()
                txtEventActiveDescription.text = title()//description()
                txtActiveEventType.text = type()

                if (status().equals("opened")) {
                    txtActiveEventStatus.text = "Active"
                }
                if (status().equals("in_progress") && smeRequest() == null) {
                    txtActiveEventStatus.text = "In Progress"
                }
                if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status() == "requested" && PreferenceUtil.getUserUniqueIdentity(mContext) != smeRequest()?.expert()) {
                    txtActiveEventStatus.text = "Awaiting SME Approval"
                }
                if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status() == "awaiting" && PreferenceUtil.getUserUniqueIdentity(mContext) != smeRequest()?.expert()) {
                    txtActiveEventStatus.text = "Awaiting SME Acceptance"
                }
                if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status() == "awaiting" && PreferenceUtil.getUserUniqueIdentity(mContext) == smeRequest()?.expert()) {
                    txtActiveEventStatus.text = " Awaiting my Decision"
                }
                if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status() == "accepted" && PreferenceUtil.getUserUniqueIdentity(mContext) != smeRequest()?.expert()) {
                    txtActiveEventStatus.text = " SME Approved"
                }
                if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status() == "accepted" && PreferenceUtil.getUserUniqueIdentity(mContext) == smeRequest()?.expert()) {
                    txtActiveEventStatus.text = " Work in Progress"
                }
                if (status().equals("closed") or status().equals("archive")) {
                    txtActiveEventStatus.text = " ARCHIVE"
                }


                txtCreatedAt.text = Utilities.ConvertUTCToLocal(Constants.TIME_FORMAT_YMDTHMS, Constants.TIME_FORMAT_DATE_OPEN_CLOSED_EVENT, createdAt().toString())
                imgViewStatus.setImageDrawable(null)
                imgViewStatus.setImageResource(0)

                if (status().equals("opened", true) && status().equals("in_progress")) {
                    txtEvent.text = "EVENT : "
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_repair_green))
                    //  holder.itemView.txtActiveEventStatus.text = mContext.getString(R.string.event_by_status, status(), owner()?.firstName() + " " + owner()?.lastName())
//                        "//"Event ${status()} by ${owner()?.firstName()} ${owner()?.lastName()}"
                } else if (status().equals("closed") or status().equals("archive")) {
                    txtEvent.text = "EVENT CLOSED : "
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_circular_button))
                } else if (status().equals("in_progress") && smeRequest() != null && smeRequest()?.status().equals("awaiting") && PreferenceUtil.getUserUniqueIdentity(mContext) == smeRequest()?.expert()) {
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_warning))

                } else {
                    txtEvent.text = "EVENT : "
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_repair_green))
                }

                if (status() == "in_progress" && smeRequest() != null) {
                    if (smeRequest()?.status() != null && smeRequest()?.parameters()?.safetyIssue() == true) {

                        txtEventLabel.visibility = View.VISIBLE
                        imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_repair_red))
                    } else {
                        txtEventLabel.visibility = View.GONE
                        imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_repair_green))
                    }
                } else {
                    txtEventLabel.visibility = View.GONE
                }
                setOnClickListener {
                    listener.onItemClick(identity())
                }
            }
        }

    }

//    fun add(events: ArrayList<EventsByUserQuery.Event>) {
//        if (listData.isEmpty()) {
//            listData.addAll(events)
//            notifyDataSetChanged()
//        } else {
//            val old = listData.size
//            listData.addAll(events)
//            notifyItemRangeInserted(old + 1, events.size)
//        }
//    }


    override fun getItemCount(): Int {

        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
