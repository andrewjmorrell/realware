package com.pivot.pivot360.screens.tasks.userTask

import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.content.listeners.OnTaskTimerItemClickListener
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.task.TaskConversationModel
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.row_item_task_complete_view.view.*
import kotlinx.android.synthetic.main.row_item_task_result.view.*
import kotlinx.android.synthetic.main.row_item_task_summary_switch.view.*
import kotlinx.android.synthetic.main.row_item_task_timer.view.*
import kotlinx.android.synthetic.main.row_item_task_view_one.view.*
import kotlinx.android.synthetic.main.row_item_task_view_one.view.tvResult
import java.util.*
import java.util.concurrent.TimeUnit


class TaskExecuteAdapter(private val mTaskConversationList: ArrayList<TaskConversationModel>,
                              private val listener: OnTaskTimerItemClickListener, var viewOnly: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ROW_ITEM_TOGGLE = 1
        const val ROW_ITEM_RESULT = 2
        const val ROW_ITEM_HEADER = 3
        const val ROW_ITEM_VIEW_ONE = 4
        const val ROW_ITEM_VIEW_COMPLETE = 5
        const val ROW_ITEM_VIEW_TIMER = 6
        const val ROW_ITEM_VIEW_SWITCH = 7

        var isCountdownRunning: Boolean = false
        var isStopwatchRunning: Boolean = false
        internal var MillisecondTime: Long = 0
        internal var StartTime: Long = 0


        var handler = Handler()

        // for countdown
        private val TIME_IN_MILLIS = 60 * 1000L
        private var mTimerRunning = false
        private var mCountDownTimer: CountDownTimer? = null
        private var mUntilFinish = TIME_IN_MILLIS


        // for stopwatch
        private var isStopWatchRunning = false
        private var mStopWatchTask: TimerTask? = null
        private var mStopWatchTimer: Timer? = null
        private var mStopWatchHandler = Handler()
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
            ROW_ITEM_VIEW_SWITCH -> SwitchViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_item_task_summary_switch,
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
            else -> HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_header, parent, false))
        }


    override fun getItemCount() = mTaskConversationList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            mTaskConversationList[position].also {
                when (holder) {
                    is ViewOneHolder -> {
                        tvResult.text = it.description.fromHtml()
                        if (it.taskStatus == Constants.TASK_STATUS_COMPLETED) {
                            imgCheck.visibility = View.VISIBLE
                        } else {
                            imgCheck.visibility = View.GONE
                        }
                    }
                    is SwitchViewHolder -> {
                        with(swTaskConversation) {
                            text = it.description.fromHtml()
                            isChecked = it.switchValue
                            isEnabled = !viewOnly
                        }
                        swTaskConversation.setOnCheckedChangeListener { compoundButton, b ->
                            var map = HashMap<String, String>()
                            map["index"] = it.checkBoxIndex
                            map["value"] = b.valueToString()
                            listener.onTaskTimerItemClick(map)
                        }



                    }
                    is ResultHolder -> {
                        txtStep.text = it.description.fromHtml()
                        txtStepDate.text = it.updatedAt
                        if (it.status == Constants.TASK_STATUS_FAILED) {
                            cardViewResult.setCardBackgroundColor(Color.parseColor("#F3BF0E"))
                            txtStepDate.setTextColor(Color.BLACK)
                            txtStep.setTextColor(Color.BLACK)
                        } else {
                            cardViewResult.setCardBackgroundColor(Color.parseColor("#5493F4"))
                            txtStepDate.setTextColor(Color.WHITE)
                            txtStep.setTextColor(Color.WHITE)
                        }

                    }
                    is CompleteViewHolder -> {
                        txtCompleteText.text = it.description.fromHtml()
                    }
                    is TimerViewHolder -> {
                        tvResult.text = it.description.fromHtml()
                        txtTimerCardTitle.text = it.title.fromHtml()
                        txtTimerCardValue.text = it.value.timeFormat()
                        txtTimerCardValue.setTextColor(Color.BLACK)
                        when (it.status) {
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
                            Constants.TASK_STATUS_STOPPED -> {

                                txtTimerStatus.text = "Countdown stopped early "
                                txtTimerCardValue.setTextColor(Color.BLACK)
                                txtTimerStatus.setTextColor(Color.parseColor("#F3BF0E"))
                                imgTimerPlayButton.setImageResource(R.drawable.ic_stop)
                                imgTimerPlayButton.setColorFilter(Color.parseColor("#F3BF0E"), android.graphics.PorterDuff.Mode.SRC_IN)

                            }
                            Constants.TASK_STATUS_NOT_STARTED -> {
                                txtTimerStatus.text = "Start Test"
                                txtTimerStatus.setTextColor(ContextCompat.getColor(txtTimerStatus.context, R.color.black))
                            }
                        }


                        ////// timer and countdown button click
                        imgTimerPlayButton.setOnClickListener { view ->
                            if (it.status != Constants.TASK_STATUS_FAILED || it.status != Constants.TASK_STATUS_STOPPED) {
                                if (it.cardType == Constants.TASK_CARD_TYPE_COUNTDOWN) {
                                    startCountdown(txtTimerCardValue, imgTimerPlayButton, txtTimerStatus, it.value.timeFormat().timeFormatToLong())
                                } else {
                                    callStopwatch(txtTimerCardValue, imgTimerPlayButton, txtTimerStatus)
                                }

                            }


                        }
                        if (viewOnly) {
                            imgTimerPlayButton.visibility = View.GONE
                        } else {
                            imgTimerPlayButton.visibility = View.VISIBLE

                        }

                    }
                }
            }
        }
    }

    //    private fun switchValueBtoS(value: Boolean): String {
//        return when (value) {
//            true -> {
//                "yes"
//            }
//            false -> {
//                "no"
//            }
//
//        }
//    }
    private fun callStopwatch(txtTimerCardValue: TextView?, imgTimerPlayButton: ImageView, txtTimerStatus: TextView) {


        if (isStopwatchRunning) {
            pauseStopWatch()
            txtTimerStatus.text = "Start Test"
            txtTimerStatus.setTextColor(ContextCompat.getColor(txtTimerStatus.context, R.color.black))
            imgTimerPlayButton.setImageResource(R.drawable.ic_play_circle_outline)
            imgTimerPlayButton.setColorFilter(ContextCompat.getColor(imgTimerPlayButton.context, R.color.color_green), android.graphics.PorterDuff.Mode.SRC_IN)
            isStopwatchRunning = false
            listener.onTaskTimerItemClick(txtTimerCardValue?.text.toString().timeFormatToLong().toString())
        } else {

            StartTime = SystemClock.uptimeMillis()
            startStopWatch(txtTimerCardValue!!)
            txtTimerStatus.text = "Stop the clock"
            txtTimerStatus.setTextColor(ContextCompat.getColor(txtTimerStatus.context, R.color.black))
            imgTimerPlayButton.setImageResource(R.drawable.ic_stop)
            imgTimerPlayButton.setColorFilter(ContextCompat.getColor(imgTimerPlayButton.context, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            isStopwatchRunning = true
        }

    }


    private fun startCountdown(txtTimerCardValue: TextView, imgTimerPlayButton: ImageView, txtTimerStatus: TextView, value: Long) {

        if (isCountdownRunning) {
            stopCountDownTimer(txtTimerCardValue)
            txtTimerStatus.text = "Start Test"
            txtTimerStatus.setTextColor(ContextCompat.getColor(txtTimerStatus.context, R.color.black))
            imgTimerPlayButton.setImageResource(R.drawable.ic_play_circle_outline)
            imgTimerPlayButton.setColorFilter(ContextCompat.getColor(imgTimerPlayButton.context, R.color.color_green), android.graphics.PorterDuff.Mode.SRC_IN)
            isCountdownRunning = false
            // listener.onTaskTimerItemClick(txtTimerCardValue.text.toString().timeFormatToLong().toString())
        } else {

            StartTime = SystemClock.uptimeMillis()

            startCountDownTimer(value, txtTimerCardValue)
            txtTimerStatus.text = "skip the countdown"
            txtTimerStatus.setTextColor(ContextCompat.getColor(txtTimerStatus.context, R.color.black))
            imgTimerPlayButton.setImageResource(R.drawable.ic_forward)
            imgTimerPlayButton.setColorFilter(Color.parseColor("#F3BF0E"), android.graphics.PorterDuff.Mode.SRC_IN)
            isCountdownRunning = true
        }


    }


    override fun getItemViewType(position: Int) = when (mTaskConversationList[position].modelType) {
        TaskConversationModel.TYPE_HEADER -> ROW_ITEM_HEADER
        TaskConversationModel.TYPE_TOGGLE -> ROW_ITEM_TOGGLE
        TaskConversationModel.TYPE_RESULT -> ROW_ITEM_RESULT
        TaskConversationModel.TYPE_VIEW_ONE -> ROW_ITEM_VIEW_ONE
        TaskConversationModel.TYPE_VIEW_COMPLETE -> ROW_ITEM_VIEW_COMPLETE
        TaskConversationModel.TYPE_VIEW_SWITCH -> ROW_ITEM_VIEW_SWITCH
        TaskConversationModel.TYPE_VIEW_TIMER -> ROW_ITEM_VIEW_TIMER
        else -> ROW_ITEM_RESULT
    }

    fun update(item: TaskConversationModel) {
        mTaskConversationList.add(item)
        notifyItemInserted(mTaskConversationList.size - 1)
    }


    inner class ResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ToggleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ViewOneHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class CompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    private fun startStopWatch(tvTimer: TextView) {
        isStopWatchRunning = true
        val currentTime = System.currentTimeMillis()

        mStopWatchTask = object : TimerTask() {
            override fun run() {
                mStopWatchHandler.post {
                    updateStopWatchView(System.currentTimeMillis() - currentTime, tvTimer)
                }
            }
        }

        mStopWatchTimer = Timer()
        mStopWatchTimer?.scheduleAtFixedRate(mStopWatchTask, 0, 10L)
    }

    private fun updateStopWatchView(millis: Long, tvStopWatch: TextView) {
        val min = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds((millis - TimeUnit.MINUTES.toMillis(min)))
        val mili =
            millis - (TimeUnit.MINUTES.toMillis(min) + TimeUnit.SECONDS.toMillis(seconds))


        tvStopWatch.text =
            String.format(Locale.getDefault(), "%02d:%02d.%02d", min, seconds, mili / 10)


    }

    private fun pauseStopWatch() {
        mStopWatchTimer?.cancel()
        isStopWatchRunning = false

    }

    private fun stopCountDownTimer(tvCountDownTime: TextView) {

        mTimerRunning = false
        mCountDownTimer?.cancel()
        listener.onTaskTimerItemClick(tvCountDownTime.text.toString().timeFormatToLong().toString())

    }

    private fun startCountDownTimer(value: Long, tvCountDownTime: TextView) {

        mCountDownTimer = object : CountDownTimer(value, 100) {

            override fun onTick(millisUntilFinished: Long) {
                updateCountDownView(millisUntilFinished, tvCountDownTime)
            }

            override fun onFinish() {

                tvCountDownTime.text =
                    String.format(Locale.getDefault(), "%02d:%02d.%02d", 0, 0, 0)
                isCountdownRunning = false
                stopCountDownTimer(tvCountDownTime)


            }

        }

        mCountDownTimer?.start()
    }

    private fun updateCountDownView(mUntilFinish: Long, tvCountDownTime: TextView) {
        val min = TimeUnit.MILLISECONDS.toMinutes(mUntilFinish)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds((mUntilFinish - TimeUnit.MINUTES.toMillis(min)))
        val millis =
            mUntilFinish - (TimeUnit.MINUTES.toMillis(min) + TimeUnit.SECONDS.toMillis(seconds))

        tvCountDownTime.text =
            String.format(Locale.getDefault(), "%02d:%02d.%02d", min, seconds, millis / 10)
    }
}