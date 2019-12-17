package com.pivot.pivot360.screens.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.pivoteye.CustomModel
import com.pivot.pivot360.pivotglass.R

class TaskViewAdapter(private val mCustomList: ArrayList<CustomModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ROW_ITEM_TOGGLE = 1
        const val ROW_ITEM_RESULT = 2
        const val ROW_ITEM_HEADER = 3
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =

            when (viewType) {
                ROW_ITEM_RESULT -> ResultHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.row_item_result,
                                parent,
                                false
                        )
                )
                ROW_ITEM_TOGGLE -> ToggleHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.row_item_toggle,
                                parent,
                                false
                        )
                )
                ROW_ITEM_HEADER -> HeaderHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.row_item_header,
                                parent,
                                false
                        )
                )
                else -> HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_header, parent, false))
            }


    override fun getItemCount() = mCustomList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }


    override fun getItemViewType(position: Int) = when (mCustomList[position].modelType) {
        CustomModel.TYPE_HEADER -> ROW_ITEM_HEADER
        CustomModel.TYPE_TOGGLE -> ROW_ITEM_TOGGLE
        CustomModel.TYPE_RESULT -> ROW_ITEM_RESULT
        else -> ROW_ITEM_RESULT
    }


    inner class ResultHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ToggleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}