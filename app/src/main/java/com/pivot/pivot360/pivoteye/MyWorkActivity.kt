package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.pivot.pivot360.pivotglass.R

class MyWorkActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_mywork)

    }

    fun onMyEventsClick(view: View) {
        val intent = Intent(this, EventsActivity::class.java)
        startActivity(intent)
    }

    fun onMyTasksClick(view: View) {
        val intent = Intent(this, TasksActivity::class.java)
        startActivity(intent)
    }
}