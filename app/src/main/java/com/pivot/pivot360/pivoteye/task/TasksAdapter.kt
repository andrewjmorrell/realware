package com.pivot.pivot360.screens.tasks

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.content.graphql.UserTasksQuery
import com.pivot.pivot360.content.listeners.OnTaskItemClickListener
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivoteye.convertUTCToLocal
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
        listData.let {
            it!![position].also { item ->
                holder.itemView.apply {
                    txtTitle.text = item.task()?.title()
                    when (item.status()) {
                        Constants.TASK_STATUS_NOT_STARTED -> {
                            txt_status_value.text = "Not Started"
                            txt_status_value.setTextColor(Color.RED)
                            civ_image.visibility = View.INVISIBLE
                            viewHorizontalBar.visibility = View.VISIBLE
                            txtTitleStatus.visibility = View.VISIBLE


                        }
                        Constants.TASK_STATUS_IN_PROGRESS -> {
                            viewHorizontalBar.visibility = View.VISIBLE
                            txt_status_value.text = "In Progress"
                            txt_status_value.setTextColor(Color.GRAY)
                            civ_image.visibility = View.VISIBLE
                            civ_image.background = ContextCompat.getDrawable(context, R.drawable.reddot)
                            txtTitleStatus.visibility = View.VISIBLE

                        }
                        Constants.TASK_STATUS_COMPLETED -> {
                            viewHorizontalBar.visibility = View.VISIBLE
                            txt_status_value.text = "Completed"
                            txt_status_value.setTextColor(Color.BLACK)
                            civ_image.visibility = View.INVISIBLE
                            txtTitleStatus.visibility = View.VISIBLE

                        }
                        else -> {
                            civ_image.visibility = View.INVISIBLE
                            viewHorizontalBar.visibility = View.INVISIBLE
                            txtTitleStatus.visibility = View.INVISIBLE
                        }

                    }
                    txtDescription.text = item.task()?.summary()
                    if (item.updatedAt() != null) {
                        txtDate.text = String.format("%s %s", "Last Updated:", "${item.updatedAt()?.convertUTCToLocal(Constants.TIME_FORMAT_YMDTHMS, Constants.TIME_FORMAT_TASKS)} by ${item.updatedBy()?.firstName()} ${item.updatedBy()?.lastName()}")
                    }
                    this.setOnClickListener {
                        listener.onTaskItemClick(item)
                    }
                }
            }
        }

    }


    fun updateAll(userTasks: List<UserTasksQuery.UserTask>) {
        listData?.clear()
        notifyDataSetChanged()
        listData?.addAll(userTasks)
        notifyDataSetChanged()
    }

    fun add(events: ArrayList<UserTasksQuery.UserTask>) {
        if (listData?.isEmpty()!!) {
            listData?.addAll(events)
            notifyDataSetChanged()
        } else {
            val old = listData?.size
            listData?.addAll(events)
            notifyItemRangeInserted(old?.plus(1)!!, events.size)
        }
    }

    fun clear() {
        listData?.clear()
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
