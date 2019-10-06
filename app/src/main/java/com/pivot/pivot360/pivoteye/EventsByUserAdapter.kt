package com.pivot.pivot360.pivoteye

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.content.graphql.EventsByUserQuery
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.active_event_list_item.view.*


class EventsByUserAdapter(context: FragmentActivity, events: ArrayList<EventsByUserQuery.Event>, myListener: AdapterView.OnItemClickListener) : RecyclerView.Adapter<EventsByUserAdapter.ViewHolder>() {

    private var listData: ArrayList<EventsByUserQuery.Event> = events
    private var mContext: Context = context
    private var listener: AdapterView.OnItemClickListener = myListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.active_event_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(listData[position]) {
            holder.itemView.apply {
                txtActiveEventTitle.text = title()
                txtActiveEventTitle.contentDescription = "hf_use_description|" + title()
                txtEventActiveDescription.text = description()
                txtActiveEventType.text = type()
                txtActiveEventStatus.text = status()
//                txtCreatedAt.text = Utilities.convertDateOpenEvent(createdAt().toString())
                if (status().equals("opened", true)) {
                    //txtEvent.text = "EVENT : "
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_repair_green))
                    //  holder.itemView.txtActiveEventStatus.text = mContext.getString(R.string.event_by_status, status(), owner()?.firstName() + " " + owner()?.lastName())
//                        "//"Event ${status()} by ${owner()?.firstName()} ${owner()?.lastName()}"
                } else {
                    //txtEvent.text = "EVENT CLOSED : "
                    imgViewStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_circular_button))
                }
                setOnClickListener {
                    //listener.onItemClick(identity())
                }
            }
        }

    }

    fun add(events: ArrayList<EventsByUserQuery.Event>) {
        if (listData.isEmpty()) {
            listData.addAll(events)
            notifyDataSetChanged()
        } else {
            val old = listData.size
            listData.addAll(events)
            notifyItemRangeInserted(old + 1, events.size)
        }
    }


    override fun getItemCount(): Int {

        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
