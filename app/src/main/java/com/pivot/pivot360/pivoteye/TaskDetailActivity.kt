package com.pivot.pivot360.pivoteye

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.activity_task_detail.*


class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        var data = intent.getBundleExtra("data")
        var desc = data.getString("description") as String

        txtDescription.text = desc.fromHtml()

    }

    fun onStartTaskClick(view: View) {
        var data = intent.getBundleExtra("data")
        startActivityWithData(TaskViewActivity(), data)
    }
}
