package com.pivot.pivot360.pivoteye.task

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import com.pivot.pivot360.content.graphql.CardSaveMutation
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.graphql.UserTaskSetupChecklistSaveMutation
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.TaskListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.Constants.TASK_STATUS_COMPLETED
import com.pivot.pivot360.pivoteye.Constants.TASK_STATUS_IN_PROGRESS
import com.pivot.pivot360.pivoteye.Constants.USER_TASK_TYPE_CONVERSATION
import com.pivot.pivot360.pivoteye.Constants.USER_TASK_TYPE_FORM
import com.pivot.pivot360.pivotglass.R
import com.pivot.pivot360.screens.tasks.userTask.TaskExecuteAdapter
import kotlinx.android.synthetic.main.activity_task_execute.*


class TaskExecuteActivity : BaseActivity(), GenericListener<Any>, TaskListener {

    private var param1: String? = null
    private var param2: String? = null
    val list = arrayListOf<TaskConversationModel>()
    private var taskStep = 0
    private var curCardId = ""
    private var isCalledFromSaveCard = false

    private var identity: String = ""
    private var token: String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_execute)
        identity = intent.getStringExtra(Constants.IDENTITY) as String
        token = intent.getStringExtra(Constants.TOKEN) as String

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        getTasks(identity)

        imgSend.setOnClickListener {
            identity?.let { callSendCardData(it, edtMessage.text.toString()) }
            hideKeyboard(this)
        }

    }

    override fun OnResults(response: Any?) {
        when (response) {
            is UserTaskQuery.Data -> {
                response.userTask().also { it ->
                    when (it) {
                        is UserTaskQuery.AsUserTaskField -> {

                            if (!isCalledFromSaveCard) {
                                onUserTaskResults(it)
                            } else {
                                if (it.status().equals(TASK_STATUS_COMPLETED, true) or it.nextCard()?.type().toString().isTimer()) {//equals(TASK_CARD_TYPE_STOPWATCH) or it.nextCard()?.type().equals(TASK_CARD_TYPE_COUNTDOWN)
                                    edtMessage.isEnabled = false
                                }
                                if (it.task()?.completedText() != null) {
                                    val itemResponse = TaskConversationModel(description = it.task()?.completedText().toString(), modelType = TaskConversationModel.TYPE_VIEW_COMPLETE)
                                    recyclerView?.let { rv ->
                                        (rv.adapter as TaskExecuteAdapter).update(itemResponse)
                                        moveRecyclerViewToBottom()
                                    }
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
                response.cardSave()?.result()?.let {
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
            is UserTaskSetupChecklistSaveMutation.Data -> {
                response.userTaskSetupChecklistSave()?.result().let {
                    when (it) {
                        is UserTaskSetupChecklistSaveMutation.AsUserTaskField -> {
                            showToast("Saved")
                        }
                        is UserTaskSetupChecklistSaveMutation.AsAuthInfoField -> {

                        }
                        is UserTaskSetupChecklistSaveMutation.AsResponseMessageField -> {
                            it.message()?.let { it1 -> showToast(it1) }
                        }
                        else -> {
                        }
                    }
                }
            }

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
                .token(token)
                .id(identity)
                .build(), this)
    }

    private fun callSendCardData(identity: String, recordedValue: String) {
        GraphQlApiHandler.instance
            .postData<CardSaveMutation, GenericListener<Any>>(
                CardSaveMutation.builder()
                    .currCardId(curCardId)
                    .step(taskStep)
                    .token(token)
                    .userTaskId(identity)
                    .value(recordedValue).build(), this)
    }

    fun onSendClick(view: View) {
        identity?.let { callSendCardData(it, edtMessage.text.toString()) }
    }

    private fun onUserTaskResults(userTask: UserTaskQuery.AsUserTaskField) {
        list.clear()

        /**
        set Data in Custom view to show different type of views in the conversation Recycler View
         */

        ////First item setupText/////
        list.add(TaskConversationModel(description = userTask.task()?.setupText()?.fromHtml().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
        /// Second item Checkbox
        userTask.resultsSetupChecklist()?.forEach {
            list.add(TaskConversationModel(description = it.description().toString(), checkBoxIndex = it.index().toString(), checkBoxValue = it.value().toString(), switchValue = it.value().toString().valueToBoolean(), modelType = TaskConversationModel.TYPE_VIEW_SWITCH))
        }
        //// third item onwards:- first add result cards. No action is required there in case of Timer or Text cards
        if (userTask.type() == USER_TASK_TYPE_CONVERSATION || (userTask.status() == TASK_STATUS_COMPLETED && userTask.type() == USER_TASK_TYPE_FORM) || (userTask.status() == TASK_STATUS_IN_PROGRESS && userTask.type() == USER_TASK_TYPE_FORM)) {
            if (userTask.results()!!.isNotEmpty()) {
                for (userTaskResult in userTask.results()!!) {

                    //////////  check for timer card  /////////
                    if (userTaskResult.card()?.type().toString().isTimer()) {
                        if (userTask.status() == TASK_STATUS_COMPLETED) {
                            list.add(TaskConversationModel(title = userTaskResult.card()?.title()!!, description = userTaskResult.card()?.description().toString(), taskType = userTask.type().toString(), cardType = userTaskResult.card()?.type().toString(), taskStatus = userTask.status().toString(), status = userTaskResult.status()!!, value = userTaskResult.value().nullToNumber(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))

                        } else {
                            //list.add(TaskConversationModel(title = userTaskResult.card()?.title()!!, description = userTaskResult.card()?.description().toString(), taskType = userTask.type().toString(), cardType = userTaskResult.card()?.type().toString(), taskStatus = userTask.status().toString(), status = userTaskResult.status()!!, value = userTaskResult.card()?.value().nullToNumber(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                            list.add(TaskConversationModel(title = userTaskResult.card()?.title()!!, description = userTaskResult.card()?.description().toString(), taskType = userTask.type().toString(), cardType = userTaskResult.card()?.type().toString(), taskStatus = userTask.status().toString(), status = userTaskResult.status()!!, value = userTaskResult.value().nullToNumber(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))

                        }
                    } else {
                        list.add(TaskConversationModel(description = userTaskResult.card()?.description().toString(), taskStatus = userTask.status().notNull(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                    }
                    /////// result card with blue background
                    if (userTaskResult.response()!!.isNotEmpty()) {
                        list.add(TaskConversationModel(status = userTaskResult.status()!!, description = userTaskResult.response().toString(), cardType = userTaskResult.card()?.type().toString(), updatedAt = userTaskResult.updatedAt().toString().convertUTCToLocal(Constants.TIME_FORMAT_YMDTHMS, Constants.TIME_FORMAT_DATE_TASK_RESPONSE), modelType = TaskConversationModel.TYPE_RESULT))
                    }
                }
                /// card which require action to perform
                if (userTask.nextCard() != null) {
                    if (userTask.nextCard()?.type().toString().isTimer()) {
                        list.add(TaskConversationModel(title = userTask.nextCard()?.title()!!, value = userTask.nextCard()?.value().notNull(), taskStatus = userTask.status().toString(), cardType = userTask.nextCard()?.type().toString(), taskType = userTask.type().toString(), description = userTask.nextCard()?.description().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                    } else {
                        list.add(TaskConversationModel(description = userTask.nextCard()?.description().toString(), taskStatus = userTask.status().notNull(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                    }

                }
                //// add last card /////
                if (userTask.status().equals(TASK_STATUS_COMPLETED, true) or userTask.nextCard()?.type().toString().isTimer()) {
                    edtMessage.isEnabled = false
                    llMessageBox.visibility = GONE
                    if (userTask.status().equals(TASK_STATUS_COMPLETED, true) && userTask.task()?.completedText() != null) {
                        list.add(TaskConversationModel(description = userTask.task()?.completedText().toString(), taskStatus = userTask.status().toString(), status = userTask.status().toString(), modelType = TaskConversationModel.TYPE_VIEW_COMPLETE))
                    }
                } else {
                    edtMessage.isEnabled = true
                    llMessageBox.visibility = VISIBLE
                }

            } else {
                ///////////////////// add this initial card if the result is null /////////////////////
                if (userTask.task()?.cards()!![0].type().toString().isTimer()) {
                    list.add(TaskConversationModel(title = userTask.task()?.cards()!![0].title()!!, value = userTask.task()?.cards()!![0].value().notNull(), taskStatus = userTask.status().toString(), cardType = userTask.task()?.cards()!![0].type().toString(), description = userTask.task()?.cards()!![0].description().toString(), taskType = userTask.type().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                    edtMessage.isEnabled = false
                    llMessageBox.visibility = GONE
                } else {
                    edtMessage.isEnabled = true
                    llMessageBox.visibility = VISIBLE
                    list.add(TaskConversationModel(description = userTask.task()?.cards()!![0].description().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                }
            }
        } else { // if Task type is form
            if (userTask.formResults()!!.isNotEmpty()) {
                for (a in userTask.formResults()!!) {
                    list.add(TaskConversationModel(description = a.card()?.description().toString(), taskStatus = userTask.status().notNull(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                }
            } else {
                if (userTask.task()?.cards()!![0].type().toString().isTimer()) {
                    list.add(TaskConversationModel(title = userTask.task()?.cards()!![0].title()!!, taskStatus = userTask.status().toString(), value = userTask.task()?.cards()!![0].value().notNull(), cardType = userTask.task()?.cards()!![0].type().toString(), description = userTask.task()?.cards()!![0].description().toString(), taskType = userTask.type().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                    edtMessage.isEnabled = false
                    llMessageBox.visibility = GONE
                } else {
                    edtMessage.isEnabled = true
                    llMessageBox.visibility = VISIBLE
                    list.add(TaskConversationModel(description = userTask.task()?.cards()!![0].description().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
                }
            }
        }
        /////////// set editText hint and type //////////
        edtMessage.hint = userTask.nextCard()?.hint() ?: "Message"

//        userTask.nextCard()?.let {
//            edtMessage.inputType = if (it.hint()?.contains("Number", true)!!) {
//                InputType.TYPE_CLASS_NUMBER
//            } else {
//                InputType.TYPE_CLASS_TEXT
//            }
//        }

        llMessageBox.visibility = if (userTask.viewOnly()!! || userTask.type() == USER_TASK_TYPE_FORM) {
            View.GONE
        } else {
            View.VISIBLE
        }

        ////////////set RecyclerView //////////
        this.recyclerView?.let {
            it.adapter = TaskExecuteAdapter(list, this, userTask.viewOnly()!!)
        }
        //////////////////////////////////////

        //////set steps and current card id to be user in CardSave mutation later on
        taskStep = if (userTask.results()!!.isEmpty()) {
            0
        } else {
            userTask.results()!![userTask.results()?.lastIndex!!].step()!!.plus(1)
        }
        curCardId = if (userTask.results()!!.isEmpty()) {
            userTask.task()!!.cards()!![0].identity().toString()
        } else {
            userTask.nextCard()?.identity().toString()
        }
        ///////////////////////////////////////////////////////////////////////////////////
        //////////// Move RecyclerView to bottom after refreshing and adding new data ////
        moveRecyclerViewToBottom()
        /////////////////////////////////////////////////////////////////////////////////
    }

    override fun onTaskItemClick(item: Any) {
        if (item is String) {
            identity?.let { callSendCardData(it, item) }
        } else if (item is Map<*, *>) {
            val cbIndex = item["index"]
            val cbValue = item["value"]
            callCheckboxSave(cbIndex.toString().notNull(), cbValue.toString().notNull())
        }
    }

    private fun callCheckboxSave(checkIndex: String, checkValue: String) {
        GraphQlApiHandler.instance
            .postData<UserTaskSetupChecklistSaveMutation, GenericListener<Any>>(
                UserTaskSetupChecklistSaveMutation.builder()
                    .checkboxIndex(checkIndex)
                    .checkboxValue(checkValue)
                    .taskId(identity)
                    .token(token)
                    .build(), this)
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

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            ActionBtnKeyCode -> {
                (recyclerView.adapter as TaskExecuteAdapter).onActionButtonPressed(keyCode, event)
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onShowHideMessageBox(show: Boolean) {
        if (show) {
            llMessageBox.visibility = VISIBLE
            edtMessage.requestFocus()
            showKeyboard(this)
            moveRecyclerViewToBottom()
        } else {
            llMessageBox.visibility = GONE
            hideKeyboard(this)
            moveRecyclerViewToBottom()
        }
    }

    override fun onTaskDataEntryClick(view: View) {
        onShowHideMessageBox(false)
        onSendClick(view)
    }
}
