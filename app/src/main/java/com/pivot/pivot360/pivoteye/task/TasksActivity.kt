package com.pivot.pivot360.pivoteye.task

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pivot.pivot360.content.graphql.TasksByAssetQuery
import com.pivot.pivot360.content.graphql.UserTasksQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.content.listeners.OnTaskItemClickListener
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivotglass.R
import com.pivot.pivot360.screens.tasks.TasksAdapter
import kotlinx.android.synthetic.main.activity_tasks.*

class TasksActivity : BaseActivity(), OnTaskItemClickListener, GenericListener<Any> {

    private var totalPages = 0

    override fun onTaskItemClick(userTask: Any) {

        if (userTask is UserTasksQuery.UserTask?) {
            startActivity(Intent(this, TaskActivity::class.java).putExtra(Constants.IDENTITY, userTask.identity()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        getTasks()
        //getTasksByAssets("b5551d4c31de4d1d3a126052446ce55")

        rvTasks.apply {
            layoutManager = LinearLayoutManager(this@TasksActivity)
            adapter = TasksAdapter(this@TasksActivity, ArrayList(), this@TasksActivity)
        }
        runLayoutAnimation(rvTasks)
    }

    private fun getTasks() {
        GraphQlApiHandler.instance
            .getData<UserTasksQuery, GenericListener<Any>>(UserTasksQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .accountId(
                    PreferenceUtil.getUserUniqueIdentity(
                        this
                    )
                )
                .build(), this)
    }

    private fun getTasksByAssets(assetid: String) {
        GraphQlApiHandler.instance
            .getData<TasksByAssetQuery, GenericListener<Any>>(TasksByAssetQuery.builder()
                .token(PreferenceUtil.getToken(this)!!)
                .assetId(assetid)
                .count(false)
                .page(1)
                .build(), this)
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is UserTasksQuery.Data -> {
                response.userTasks().let {
                    when (it) {
                        is UserTasksQuery.AsUserTaskResults -> {
                            onUserTaskResults(it)
                        }

                        is UserTasksQuery.AsResponseMessageField -> {

                        }
                        is UserTasksQuery.UserTasks -> {

                        }
                    }
                }
            }
            is TasksByAssetQuery.Data -> {
                response.tasksByAsset().let { item ->
                    when (item) {
                        is TasksByAssetQuery.AsTaskResults -> {
                            item.tasks()?.let { onAssetTaskResults(it, item.total()) }
                        }
                        is TasksByAssetQuery.AsResponseMessageField -> {
                            showToast(item.message()!!)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun onUserTaskResults(it: UserTasksQuery.AsUserTaskResults) {
        (rvTasks.adapter as TasksAdapter).updateAll(it.userTasks()!!)
        runOnUiThread {
            (rvTasks.adapter as TasksAdapter).notifyDataSetChanged()
        }
    }

    private fun onAssetTaskResults(it: MutableList<TasksByAssetQuery.Task>, total: Int?) {
//        var list = ArrayList<UserTasksQuery.UserTask>()
//        for (a in it) {
//            var dd = Gson().toJson(a)
//            var taskss = Gson().fromJson<UserTasksQuery.Task>(dd, UserTasksQuery.Task::class.java)
//            var b: UserTasksQuery.UserTask = UserTasksQuery.UserTask(a.__typename(), a.identity(), taskss, null, null, null, null, null, a.createdAt()!!, null, null, null)
//
//
//            list.add(b)
//        }
//        totalPages = kotlin.math.ceil((total!! / 20).toDouble()).toInt()
//        (rvTasks.adapter as TasksAdapter).updateAll(list)
//        runOnUiThread {
//            (rvTasks.adapter as TasksAdapter).notifyDataSetChanged()
//        }


//        var c = Gson().toJson(it)
//        var d = Gson().toJson(it)
//        val type = object : TypeToken<ArrayList<UserTasksQuery.UserTask>>() {}.type
//        val yourList: ArrayList<UserTasksQuery.UserTask> = Gson().fromJson(c, type)
//
//        for (a in it!!) {
//            var dd = Gson().toJson(a)
//            var taskss = Gson().fromJson<UserTasksQuery.Task>(dd, UserTasksQuery.Task::class.java)
//            //var task=UserTasksQuery.Task(a.__typename(),a.identity(),a.title(),a.asset()!!,a.description(),a.startCards()!!,a.createdAt())
//            var b: UserTasksQuery.UserTask = UserTasksQuery.UserTask(a.__typename(), a.identity(), taskss, null, null, null, a.createdAt()!!, null, null)
//
//
//            list.add(b)
//        }
//        (rvTasks.adapter as TasksAdapter).updateAll(list)
//        runOnUiThread {
//            (rvTasks.adapter as TasksAdapter).notifyDataSetChanged()
//        }
    }

    override fun onAuthInfoField(message: String?) {
        showToast(message!!)

    }

    override fun onError(message: String?) {
        showToast(message!!)
    }

    override fun onResponseMessageField(message: String?) {
        showToast(message!!)
    }
}