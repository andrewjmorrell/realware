package com.pivot.pivot360.content.listeners

import com.pivot.pivot360.content.graphql.UserTasksQuery

interface OnTaskItemClickListener {
    fun onTaskItemClick(item: UserTasksQuery.Task?)
}