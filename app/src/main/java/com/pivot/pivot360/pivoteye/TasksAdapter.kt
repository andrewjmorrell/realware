package com.pivot.pivot360.screens.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.content.graphql.UserTasksQuery
import com.pivot.pivot360.content.listeners.OnTaskItemClickListener
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.row_item_tasks.view.*


class TasksAdapter(context1: FragmentActivity, attachments: MutableList<UserTasksQuery.UserTask>?, var listener: OnTaskItemClickListener) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {


    var listData = attachments
    internal var context: Context = context1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.row_item_tasks, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.itemView.civ_image.background = ContextCompat.getDrawable(context, R.drawable.reddot)
                holder.itemView.setOnClickListener {
                    listener.onTaskItemClick(listData!![position].task())
                }
            }
            else -> {
                holder.itemView.civ_image.visibility = View.INVISIBLE
            }
        }
        listData.let {
            it!![position].apply {
                holder.itemView.apply {
                    txtTitle.text = task()?.title()
                    txtDescription.text = task()?.summary()
                    txtDate.text = String.format("%s %s", "Last Updated:", "${task()?.createdAt()} by Sam Richardson")
                    this.setOnClickListener {
                        listener.onTaskItemClick(task())
                    }
                }
            }
        }

    }


    fun updateAll(userTasks: List<UserTasksQuery.UserTask>) {
        listData?.clear()
        listData?.addAll(userTasks)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
