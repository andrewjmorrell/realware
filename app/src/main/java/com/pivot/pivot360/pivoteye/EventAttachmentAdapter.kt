package com.pivot.pivot360.pivoteye

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.common.Utilities
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.item_list_attachment_v2.view.*


class EventAttachmentAdapter(context1: FragmentActivity, attachments: MutableList<EventQuery.Attachment>?, myListener: OnItemClickListener) : RecyclerView.Adapter<EventAttachmentAdapter.ViewHolder>() {


    var listData = attachments
    internal var context: Context = context1
    private val listener: OnItemClickListener = myListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.item_list_attachment_v2, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = listData!![position]
        with(listData!![position]) {

            holder.itemView.apply {
                txtAttachmentName.text = name()
                txtAttachmentType.text = ""
                // holder.itemView.txtAttachmentType.text = "${type()} : "
                when {
                    fileType().equals("image", true) -> {
                        iv_attachment_img.background = ContextCompat.getDrawable(context, R.drawable.ic_image_black_24dp)
                    }
                    fileType().equals("video", true) -> {
                        iv_attachment_img.background = ContextCompat.getDrawable(context, R.drawable.ic_videocam_black_24dp)
                    }
                }
                setOnClickListener {
                    listener.onItemClick(link())
                }

                tv_attachment_date.text = String.format("%s", "Updated on  ${Utilities.convertDateAttachmentsNew(updatedAt()!!)}")
                // holder.itemView.iv_attachment_img.background = ContextCompat.getDrawable(context, image)
            }
        }

    }


    override fun getItemCount(): Int {

        return listData!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
