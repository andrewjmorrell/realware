package com.pivot.pivot360.pivoteye

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.api.Input
import com.pivot.pivot360.content.graphql.TaskQuery
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.graphql.UserTasksQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import com.pivot.pivot360.screens.tasks.TaskViewAdapter
import kotlinx.android.synthetic.main.activity_task_view.*

class TaskViewActivity : AppCompatActivity(), GenericListener<Any> {
    override fun onError(message: String?) {
        showToast(message!!)
    }

    override fun onAuthInfoField(message: String?) {
        showToast(message!!)
    }

    override fun onResponseMessageField(message: String?) {
        showToast(message!!)
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

    fun onUserTaskResults(item: UserTaskQuery.UserTask?) {

    }

    private var identity: String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_view)
        identity = intent.getBundleExtra("data").getString(Constants.IDENTITY) as String

        GraphQlApiHandler.instance
            .getData<UserTaskQuery, GenericListener<Any>>(
                UserTaskQuery.builder().token(PreferenceUtil.getToken(this)!!)
                    .id(identity).build(), this)

        val list = arrayListOf<CustomModel>().apply {
            add(CustomModel(modelType = CustomModel.TYPE_HEADER))
            add(CustomModel(modelType = CustomModel.TYPE_TOGGLE))
            add(CustomModel(modelType = CustomModel.TYPE_TOGGLE))
            add(CustomModel(modelType = CustomModel.TYPE_TOGGLE))
            add(CustomModel(modelType = CustomModel.TYPE_TOGGLE))
            add(CustomModel(modelType = CustomModel.TYPE_HEADER))
            add(CustomModel(modelType = CustomModel.TYPE_RESULT))
            add(CustomModel(modelType = CustomModel.TYPE_RESULT))
            add(CustomModel(modelType = CustomModel.TYPE_RESULT))
            add(CustomModel(modelType = CustomModel.TYPE_RESULT))
            add(CustomModel(modelType = CustomModel.TYPE_RESULT))
        }
        recyclerView.adapter = TaskViewAdapter(list)
    }
}
