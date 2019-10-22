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
import kotlinx.android.synthetic.main.user_list_item.view.*

class LoginAdapter(context: FragmentActivity, users: ArrayList<String>, myListener: OnItemClickListener) : RecyclerView.Adapter<LoginAdapter.ViewHolder>() {
    private var listData: ArrayList<String> = users
    private var mContext: Context = context
    private var listener: OnItemClickListener = myListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.user_list_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.username.text = listData[position]
        holder.itemView.setOnClickListener {
            listener.onItemClick(listData[position])
        }

    }

    override fun getItemCount(): Int {

        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
