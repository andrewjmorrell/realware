package com.pivot.pivot360.pivoteye.task

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.content.listeners.OnTaskTimerItemClickListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_task_view.*


class TaskViewActivity : AppCompatActivity(), GenericListener<Any>, OnTaskTimerItemClickListener {

    lateinit var identity: String

    lateinit var mUserTask: UserTaskQuery.AsUserTaskField

    val list = arrayListOf<TaskConversationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_view)

        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            getTask(identity)
        } else {
            Toast.makeText(this, "Task id not found.", Toast.LENGTH_SHORT).show()
        }

    }

    fun getTask(identity: String) {
        GraphQlApiHandler.instance
            .getData<UserTaskQuery, GenericListener<Any>>(UserTaskQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .id(identity)
                .build(), this)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is UserTaskQuery.Data -> {
                response.userTask().let {
                    when (it) {
                        is UserTaskQuery.AsUserTaskField -> {
                            onUserTaskResults(it)
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
        }
    }

    private fun onUserTaskResults(it: UserTaskQuery.AsUserTaskField) {


        list.clear()


        /**
        set Data in Custom view to show different type of views in the conversation Recycler View
         */


        if (it.results()?.isNotEmpty()!!) {


            //
            var summary = it.results()!!.filter { it.card()?.isFormCard == true }
            for (a in summary) {
                ////////// As Found /////////////
                if (a.card()?.type() == Constants.TASK_CARD_TYPE_TEXT) {
                    list.add(TaskConversationModel(title = a?.card()?.title().toString(), description = a?.value().toString(), modelType = TaskConversationModel.TYPE_HEADER_FOUND_AS))
                }
                ////////////// Test Result /////////////


                if (a.card()?.type().equals(Constants.TASK_CARD_TYPE_COUNTDOWN, true) or a.card()?.type().equals(Constants.TASK_CARD_TYPE_STOPWATCH, true)) {
                    list.add(TaskConversationModel(title = a.card()?.title()!!, description = a.card()?.description().toString(), status = a.status()!!, value = a.value().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                }

            }

            if (it.nextCard() != null) {
                if (it.nextCard()?.type() == Constants.TASK_CARD_TYPE_TEXT) {
                    list.add(TaskConversationModel(title = it.nextCard()?.title().toString(), description = it.nextCard()?.value().toString(), modelType = TaskConversationModel.TYPE_HEADER_FOUND_AS))
                }
                if (it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_COUNTDOWN, true) or it.nextCard()?.type().equals(Constants.TASK_CARD_TYPE_STOPWATCH, true)) {
                    list.add(TaskConversationModel(description = it.nextCard()?.description().toString(), modelType = TaskConversationModel.TYPE_VIEW_TIMER))
                }

            }
//
//            if (it.status().equals(TASK_STATUS_COMPLETED, true)) {
//                list.add(CustomModel(description = it.task()?.completedText().toString(), modelType = CustomModel.TYPE_VIEW_COMPLETE))
//            }

        } else {
            ///////////////////// add this initial card if the result is null /////////////////////
            list.add(TaskConversationModel(description = it.task()?.cards()!![0].description().toString(), modelType = TaskConversationModel.TYPE_VIEW_ONE))
        }

        val tempList = arrayListOf<TaskConversationModel>()

        tempList.add(TaskConversationModel(title = "Task Setup Checklist", modelType = TaskConversationModel.TYPE_HEADER))
        it.resultsSetupChecklist()?.forEach {
            tempList.add(TaskConversationModel(description = it?.description().toString(), checkBoxIndex = it?.index().toString(), checkBoxValue = it?.value().toString(), switchValue = it?.value().toString().valueToBoolean(), modelType = TaskConversationModel.TYPE_VIEW_SWITCH))
        }
        if (list.any { it.modelType == TaskConversationModel.TYPE_HEADER_FOUND_AS }) {
            tempList.add(TaskConversationModel(title = "Record \"As Found\" Values", modelType = TaskConversationModel.TYPE_HEADER))
            tempList.addAll(list.filter { it.modelType == TaskConversationModel.TYPE_HEADER_FOUND_AS })
        }
        if (list.any { it.modelType == TaskConversationModel.TYPE_VIEW_TIMER }) {
            tempList.add(TaskConversationModel(title = "Test Result", modelType = TaskConversationModel.TYPE_HEADER))
            tempList.addAll(list.filter { it.modelType == TaskConversationModel.TYPE_VIEW_TIMER })
        }


        ////////////set RecyclerView //////////
        this.mRecyclerViewTaskSummary.adapter = TaskViewAdapter(tempList, this, it.viewOnly())
        //////////////////////////////////////


    }

    override fun onAuthInfoField(message: String?) {
        message?.let { showToast(it) }
    }

    override fun onError(message: String?) {
        message?.let { showToast(it) }
    }

    override fun onResponseMessageField(message: String?) {
        message?.let { showToast(it) }
    }

    override fun onTaskTimerItemClick(timerValue: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
