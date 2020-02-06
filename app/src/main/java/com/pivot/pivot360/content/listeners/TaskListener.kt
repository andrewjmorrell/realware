package com.pivot.pivot360.content.listeners

import android.view.View

interface TaskListener {
    fun onTaskItemClick(item: Any)
    fun onShowHideMessageBox(show: Boolean)
    fun onTaskDataEntryClick(view: View)
}