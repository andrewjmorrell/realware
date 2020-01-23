package com.pivot.pivot360.pivoteye.task

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.content.listeners.OnTaskTimerItemClickListener
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.task.TaskConversationModel
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.row_item_header.view.*
import kotlinx.android.synthetic.main.row_item_task_as_found.view.*
import kotlinx.android.synthetic.main.row_item_task_complete_view.view.*
import kotlinx.android.synthetic.main.row_item_task_result.view.*
import kotlinx.android.synthetic.main.row_item_task_summary_switch.view.*
import kotlinx.android.synthetic.main.row_item_task_timer.view.*

class TaskViewAdapter(private val mTaskConversationList: ArrayList<TaskConversationModel>, private val listener: OnTaskTimerItemClickListener, val viewOnly: Boolean?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ROW_ITEM_TOGGLE = 1
        const val ROW_ITEM_RESULT = 2
        const val ROW_ITEM_HEADER = 3
        const val ROW_ITEM_VIEW_ONE = 4
        const val ROW_ITEM_VIEW_COMPLETE = 5
        const val ROW_ITEM_VIEW_TIMER = 6
        const val ROW_ITEM_VIEW_SWITCH = 7
        const val ROW_ITEM_VIEW_FOUNDAS = 8

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =

        when (viewType) {
            ROW_ITEM_RESULT -> ResultHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_result,
                    parent,
                    false
                )
            )
            ROW_ITEM_TOGGLE -> ToggleHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_toggle,
                    parent,
                    false
                )
            )
            ROW_ITEM_HEADER -> HeaderHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_header,
                    parent,
                    false
                )
            )
            ROW_ITEM_VIEW_ONE -> ViewOneHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_view_one,
                    parent,
                    false
                )
            )
            ROW_ITEM_VIEW_COMPLETE -> CompleteViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_complete_view,
                    parent,
                    false
                )
            )
            ROW_ITEM_VIEW_TIMER -> TimerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_timer,
                    parent,
                    false
                )
            )
            ROW_ITEM_VIEW_SWITCH -> SwitchViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_summary_switch,
                    parent,
                    false
                )
            )
            ROW_ITEM_VIEW_FOUNDAS -> FoundAsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_as_found,
                    parent,
                    false
                )
            )
            else -> HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_header, parent, false))
        }


    override fun getItemCount() = mTaskConversationList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.apply {
            mTaskConversationList[position].also {
                when (holder) {
                    is HeaderHolder -> {
                        txtTaskHeader.text = mTaskConversationList[position].title
                    }
                    is SwitchViewHolder -> {
                        with(swTaskConversation) {
                            text = it.description.fromHtml()
                            isChecked = it.switchValue
                            isEnabled = !viewOnly!!

                        }
                        swTaskConversation.setOnCheckedChangeListener { compoundButton, b ->
                            var map = HashMap<String, String>()
                            map["index"] = it.checkBoxIndex
                            map["value"] = b.valueToString()
                            listener.onTaskTimerItemClick(map)
                        }
                    }

                    is FoundAsViewHolder -> {
                        txtFoundAsTitle.text = mTaskConversationList[position].title
                        txtFoundAsDescription.text = mTaskConversationList[position].description.notNull()

                    }
                    is ViewOneHolder -> {
                        tvResult.text = mTaskConversationList[position].description.fromHtml()
                    }
                    is ResultHolder -> {
                        txtStep.text = mTaskConversationList[position].description.fromHtml()
                        txtStepDate.text = mTaskConversationList[position].updatedAt
                    }
                    is CompleteViewHolder -> {
                        txtCompleteText.text = mTaskConversationList[position].description.fromHtml()
                    }
                    is TimerViewHolder -> {
                        cardViewTimer.visibility = View.GONE
                        tvResult.text = mTaskConversationList[position].description.fromHtml()
                        txtTimerCardTitle.text = mTaskConversationList[position].title.fromHtml()
                        txtTimerCardValue.text = mTaskConversationList[position].value.timeFormat()
                        txtTimerCardValue.setTextColor(Color.BLACK)
                        when (mTaskConversationList[position].status) {
                            Constants.TASK_STATUS_COMPLETED -> {
                                txtTimerStatus.text = "Countdown Completed "
                                txtTimerCardValue.setTextColor(Color.BLACK)
                                txtTimerStatus.setTextColor(Color.parseColor("#5493F4"))
                                imgTimerPlayButton.setImageResource(R.drawable.ic_check_circular_button)
                            }
                            Constants.TASK_STATUS_FAILED -> {
                                txtTimerStatus.text = "Failed "
                                txtTimerCardValue.setTextColor(Color.BLACK)
                                txtTimerStatus.setTextColor(Color.parseColor("#F3BF0E"))
                                imgTimerPlayButton.setImageResource(R.drawable.ic_warning)
                            }
                            Constants.TASK_STATUS_PASSED -> {

                                txtTimerStatus.text = "Passed "
                                txtTimerCardValue.setTextColor(Color.BLACK)
                                txtTimerStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_green))
                                imgTimerPlayButton.setImageResource(R.drawable.ic_check_circular_button)
                                imgTimerPlayButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.color_green), android.graphics.PorterDuff.Mode.SRC_IN)

                            }
                        }

                    }
                }
            }
        }
    }


    override fun getItemViewType(position: Int) = when (mTaskConversationList[position].modelType) {
        TaskConversationModel.TYPE_HEADER -> ROW_ITEM_HEADER
        TaskConversationModel.TYPE_TOGGLE -> ROW_ITEM_TOGGLE
        TaskConversationModel.TYPE_RESULT -> ROW_ITEM_RESULT
        TaskConversationModel.TYPE_VIEW_ONE -> ROW_ITEM_VIEW_ONE
        TaskConversationModel.TYPE_VIEW_COMPLETE -> ROW_ITEM_VIEW_COMPLETE
        TaskConversationModel.TYPE_VIEW_TIMER -> ROW_ITEM_VIEW_TIMER
        TaskConversationModel.TYPE_VIEW_SWITCH -> ROW_ITEM_VIEW_SWITCH
        TaskConversationModel.TYPE_HEADER_FOUND_AS -> ROW_ITEM_VIEW_FOUNDAS
        else -> ROW_ITEM_RESULT
    }


    inner class ResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ToggleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ViewOneHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class CompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class FoundAsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}