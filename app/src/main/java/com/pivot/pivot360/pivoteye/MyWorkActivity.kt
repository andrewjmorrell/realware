package com.pivot.pivot360.pivoteye

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.asset.AssetsActivity
import com.pivot.pivot360.pivoteye.event.EventsActivity
import com.pivot.pivot360.pivoteye.task.TasksActivity
import com.pivot.pivot360.pivotglass.R

class MyWorkActivity: BaseActivity() {

    private lateinit var mToken: String
    private lateinit var mUniqueId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_mywork)

        if (intent != null) {
            mToken = intent.getStringExtra("token")
            mUniqueId = intent.getStringExtra("uniqueid")
        } else {
            Toast.makeText(this, "Error getting user token", Toast.LENGTH_SHORT).show()
        }

    }

    fun onMyEventsClick(view: View) {
        val intent = Intent(this, EventsActivity::class.java).putExtra("token", mToken).putExtra("uniqueid", mUniqueId)
        startActivity(intent)
    }

    fun onMyTasksClick(view: View) {
        val intent = Intent(this, TasksActivity::class.java).putExtra("token", mToken).putExtra("uniqueid", mUniqueId)
        startActivity(intent)
    }

    fun onAssetsClick(view: View) {
        val intent = Intent(this, AssetsActivity::class.java).putExtra("token", mToken).putExtra("uniqueid", mUniqueId)
        startActivity(intent)
    }
}