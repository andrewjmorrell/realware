package com.pivot.pivot360.pivoteye.task

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pivot.pivot360.content.graphql.CardSaveMutation
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnTaskTimerItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.Constants.TASK_STATUS_COMPLETED
import com.pivot.pivot360.pivotglass.R
import com.pivot.pivot360.screens.tasks.userTask.TaskExecuteAdapter
import kotlinx.android.synthetic.main.activity_task_execute.*

class TaskExecuteActivity : AppCompatActivity(), GenericListener<Any>, OnTaskTimerItemClickListener {

    private var param1: String? = null
    private var param2: String? = null
    val list = arrayListOf<TaskConversationModel>()
    private var steps = 0
    private var curCardId = ""
    private var isCalledFromSaveCard = false

    private var identity: String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_execute)
        identity = intent.getStringExtra(Constants.IDENTITY) as String

        getTasks(identity)

    }

    override fun OnResults(response: Any?) {
        when (response) {
            is UserTaskQuery.Data -> {
                response.userTask()?.also {
                    when (it) {
                        is UserTaskQuery.AsUserTaskField -> {

                            if (!isCalledFromSaveCard) {
                                onUserTaskResults(it)
                            } else {
                                if (it.status().equals(TASK_STATUS_COMPLETED, true) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_STOPWATCH) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_COUNTDOWN)) {
                                    edtMessage.isEnabled = false
                                }
                                if (it.task()?.completedText() != null) {
                                    // list.add(CustomModel(description = it.task()?.completedText().toString(), modelType = CustomModel.TYPE_VIEW_COMPLETE))
                                    var itemResponse = TaskConversationModel(description = it.task()?.completedText().toString(), modelType = TaskConversationModel.TYPE_VIEW_COMPLETE)
                                    (recyclerView.adapter as TaskExecuteAdapter).update(itemResponse)
                                    moveRecyclerViewToBottom()
                                }
                            }
                        }


                        is UserTaskQuery.AsResponseMessageField -> {
                            it.message()?.let { it1 -> showToast(it1) }
                        }
                        is UserTaskQuery.Task -> {

                        }
                        else -> {
                        }
                    }
                }
            }
            is CardSaveMutation.Data -> {
                response.cardSave()?.result().let {
                    when (it) {
                        is CardSaveMutation.AsCardSaveField -> {
                            edtMessage.text.clear()
                            identity?.let { item -> getTasks(item) }
                        }
                        is CardSaveMutation.AsResponseMessageField -> {
                            it.message()?.let { it1 -> showToast(it1) }
                        }
                        else -> {
                        }
                    }
                }
            }
//            is UserTaskStetupChecklistSaveMutation.Data -> {
//                response.userTaskStetupChecklistSave()?.result().let {
//                    when (it) {
//                        is UserTaskStetupChecklistSaveMutation.AsUserTaskField -> {
//                            showToast("Saved")
//                        }
//                        is UserTaskStetupChecklistSaveMutation.AsAuthInfoField -> {
//
//                        }
//                        is UserTaskStetupChecklistSaveMutation.AsResponseMessageField -> {
//                            it.message()?.let { it1 -> showToast(it1) }
//                        }
//                        else -> {
//                        }
//                    }
//                }
//            }

        }
    }

    private fun moveRecyclerViewToBottom() {
        recyclerView.post {
            try {
                val y = recyclerView.y + recyclerView.getChildAt(list.size - 1).y
                nestedScroll.smoothScrollTo(0, y.toInt())
            } catch (e: Exception) {
            }
        }
    }

    fun getTasks(identity: String) {
        GraphQlApiHandler.instance
            .getData<UserTaskQuery, GenericListener<Any>>(UserTaskQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .id(identity)
                .build(), this)
    }

    private fun callSendCardData(identity: String, recordedValue: String) {
        GraphQlApiHandler.instance
            .postData<CardSaveMutation, GenericListener<Any>>(
                CardSaveMutation.builder()
                    .currCardId(curCardId)
                    .step(steps)
                    .token(PreferenceUtil.getToken(this)!!)
                    .userTaskId(identity)
                    .value(recordedValue).build(), this)
    }

    fun onSendClick(view: View) {
        identity?.let { callSendCardData(it, edtMessage.text.toString()) }
    }

    private fun onUserTaskResults(it: UserTaskQuery.AsUserTaskField) {
        list.clear()

        /**
        set Data in Custom view to show different type of views in the conversation Recycler View
         */
        ////First item setupText/////
        list.add(TaskConversationModel(description = it.task()?.setupText()?.fromHtml().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
        /// Second item Checkbox
        it.resultsSetupChecklist()?.forEach {
            list.add(TaskConversationModel(description = it?.description().toString(), checkBoxIndex = it?.index().toString(), checkBoxValue = it?.value().toString(), switchValue = it?.value().toString().valueToBoolean(), modelType = TaskConversationModel.TYPE_VIEW_SWITCH))
        }
        //// first add result cards.No action is required there in case of Timer or Text cards
        if (it.results()!!.isNotEmpty()) {
            for (a in it.results()!!) {
                //////////  check for timer card  /////////
                if (a.card()?.type().equals("countdown", true) or a.card()?.type().equals("stopwatch", true)) {
                    list.add(TaskConversationModel(title = a.card()?.title()!!, description = a.card()?.description().toString(), cardType = a.card()?.type().toString(), status = a.status()!!, value = a.value().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                } else {
                    list.add(TaskConversationModel(description = a?.card()?.description().toString(), taskStatus = it.status().notNull(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                }
                /////// result card with blue background
                if (a?.response()!!.isNotEmpty()) {
                    list.add(TaskConversationModel(status = a.status()!!, description = a.response().toString(), cardType = a.card()?.type().toString(), updatedAt = a.updatedAt().toString().convertUTCToLocal(Constants.TIME_FORMAT_YMDTHMS, Constants.TIME_FORMAT_DATE_TASK_RESPONSE), modelType = TaskConversationModel.TYPE_RESULT))
                }
            }
            /// card which require action to perform
            if (it.nextCard() != null) {
                if (it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_STOPWATCH, true) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_COUNTDOWN, true)) {
                    list.add(TaskConversationModel(title = it.nextCard()?.title()!!, value = it.nextCard()?.value().notNull(), cardType = it.nextCard()?.type().toString(), description = it.nextCard()?.description().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                } else {
                    list.add(TaskConversationModel(description = it.nextCard()?.description().toString(), taskStatus = it.status().notNull(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                }

            }
            //// add last card /////
            if (it.status().equals(TASK_STATUS_COMPLETED, true) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_STOPWATCH) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_COUNTDOWN)) {
                edtMessage.isEnabled = false
                if (it.task()?.completedText() != null) {
                    list.add(TaskConversationModel(description = it.task()?.completedText().toString(), status = it.status().toString(), modelType = TaskConversationModel.TYPE_VIEW_COMPLETE))
                }
            }

        } else {
            ///////////////////// add this initial card if the result is null /////////////////////
            if (it.task()?.cards()!![0].type() == Constants.TASK_CARD_TYPE_COUNTDOWN || it.task()?.cards()!![0].type() == Constants.TASK_CARD_TYPE_STOPWATCH) {
                list.add(TaskConversationModel(title = it.task()?.cards()!![0].title()!!, value = it.task()?.cards()!![0].value().notNull(), cardType = it.task()?.cards()!![0].type().toString(), description = it.task()?.cards()!![0].description().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                edtMessage.isEnabled = false
            } else {
                edtMessage.isEnabled = true
                list.add(TaskConversationModel(description = it.task()?.cards()!![0].description().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
            }
        }
        ///////////set editText hint and type
        edtMessage.hint = it.nextCard()?.hint() ?: "Entry"
        it.nextCard()?.let {
            if (it.hint()?.contains("Number", true)!!) {
                edtMessage.inputType = InputType.TYPE_CLASS_NUMBER
            } else {
                edtMessage.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
        if (it.viewOnly()!!) {
            llMessageBox.visibility = View.GONE
        } else {
            llMessageBox.visibility = View.VISIBLE
        }

        ////////////set RecyclerView //////////
        this.recyclerView.adapter = TaskExecuteAdapter(list, this, it.viewOnly()!!)
        //////////////////////////////////////

        //////set steps and current card id to be user in CardSave mutation later on
        steps = if (it.results()!!.isEmpty()) {
            0
        } else {
            it.results()!![it.results()?.lastIndex!!].step()!!.plus(1)
        }
        curCardId = if (it.results()!!.isEmpty()) {
            it.task()!!.cards()!![0].identity().toString()
        } else {
            it.nextCard()?.identity().toString()
            //it.results()!![it.results()?.lastIndex!!].card()!!.identity()!!
        }
        ////////////////////////////////////////////////////////////////////////////
        //////////// Move RecyclerView to bottom after refreshing and adding new data
        moveRecyclerViewToBottom()
        //////////////////////////////////////////////////////////////////////////////
    }

    override fun onTaskTimerItemClick(timerValue: Any) {
        if (timerValue is Map<*, *>) {
            var checkboxindex = timerValue["index"]
            var checkboxvalue = timerValue["value"]
            callCheckboxSave(checkboxindex.toString().notNull(), checkboxvalue.toString().notNull())
        }
    }

    private fun callCheckboxSave(checkIndex: String, checkValue: String) {
//        GraphQlApiHandler.instance
//            .postData<UserTaskStetupChecklistSaveMutation, GenericListener<Any>>(UserTaskStetupChecklistSaveMutation.builder()
//                .checkboxIndex(checkIndex)
//                .checkboxValue(checkValue)
//                .taskId(identity)
//                .token(PreferenceUtil.getToken(this)!!)
//                .build(), this)
    }

    override fun onError(message: String?) {
        showToast(message!!)
    }

    override fun onAuthInfoField(message: String?) {
        showToast(message!!)
    }

    override fun onResponseMessageField(message: String?) {
        showToast(message!!)
    }
}
