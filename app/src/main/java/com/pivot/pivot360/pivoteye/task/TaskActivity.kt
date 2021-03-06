package com.pivot.pivot360.pivoteye.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.pivot.pivot360.content.graphql.UserTaskQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.util.PreferenceUtil
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_task.*

/**
 * Main activity which displays a list of examples to the user
 */
class TaskActivity : Activity(), GenericListener<Any> {

    var identity: String? = null
    var token: String? = null

    lateinit var mUserTask: UserTaskQuery.AsUserTaskField

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState See Android docs
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_task)


        if (intent != null) {
            identity = intent.getStringExtra(Constants.IDENTITY)
            token = intent.getStringExtra(Constants.TOKEN)
            GraphQlApiHandler.instance
                .getData<UserTaskQuery, GenericListener<Any>>(
                    UserTaskQuery.builder()
                        .token(token!!)
                        .id(identity)
                        .build(), this)
        } else {
            Toast.makeText(this, "Task id not found.", Toast.LENGTH_SHORT).show()
        }

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

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@TaskActivity, message, Toast.LENGTH_SHORT).show() }
    }

    private fun onUserTaskResults(userTask: UserTaskQuery.AsUserTaskField) {
        runOnUiThread {

            mUserTask = userTask
            taskName.text = userTask.task()?.title()
            taskDescription.text = userTask.task()?.description()!!.fromHtml()

            var status = userTask.status()
            when (status) {
                Constants.TASK_STATUS_NOT_STARTED -> {
                    startTask.visibility = View.VISIBLE
                    completeTask.visibility = View.GONE
                    viewTask.visibility = View.GONE
                }
                Constants.TASK_STATUS_IN_PROGRESS -> {
                    startTask.visibility = View.GONE
                    completeTask.visibility = View.VISIBLE
                    viewTask.visibility = View.GONE
                }
                Constants.TASK_STATUS_COMPLETED -> {
                    startTask.visibility = View.GONE
                    completeTask.visibility = View.GONE
                    viewTask.visibility = View.VISIBLE
                }
            }
        }
    }

    fun onAttachmentsClick(view: View) {
        startActivity(Intent(this, TaskAttachmentActivity::class.java)
            .putExtra(Constants.IDENTITY, identity)
            .putExtra(Constants.TOKEN, token))
    }

    fun onStartTaskClick(view: View) {
        startActivity(Intent(this, TaskExecuteActivity::class.java)
            .putExtra(Constants.IDENTITY, identity)
            .putExtra(Constants.TOKEN, token))
    }

    fun onContinueTaskClick(view: View) {
        startActivity(Intent(this, TaskExecuteActivity::class.java)
            .putExtra(Constants.IDENTITY, identity)
            .putExtra(Constants.TOKEN, token))
    }

    fun onViewTaskClick(view: View) {
        startActivity(Intent(this, TaskViewActivity::class.java)
            .putExtra(Constants.IDENTITY, identity)
            .putExtra(Constants.TOKEN, token))
    }
}