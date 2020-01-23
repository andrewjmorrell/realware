package com.pivot.pivot360.pivoteye.task

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pivot.pivot360.content.graphql.EventsQuery
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.pivoteye.PreferenceUtil
import com.pivot.pivot360.pivoteye.Utils
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.fragment_attachment.*
import java.net.URL


/**
 * A simple [Fragment] subclass.
 */
private const val ARG_PARAM1 = "param1"
class EventAttachmentFragment : Fragment(), OnItemClickListener {


    private val DOCUMENT_REQUEST_CODE = 1890

    private var mToken: String? = null
    private var identity: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_attachment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            identity = it.getString(ARG_PARAM1)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mToken = PreferenceUtil.getToken(activity!!)
        //TODO Make identity dynamic
        //GraphQlApiHandler.instance.getEvent(EventQuery.builder().token(mToken!!).id(identity).build(), this)
        //"7cf69207ff2153faa12da2b73f697472"
        //GraphQlApiHandler.getInstance().getEvent(GetEventQuery.builder().token(mToken!!).id(activity?.intent?.getStringExtra("identity")).build(), this)
        mRecyclerViewAttachment.layoutManager = LinearLayoutManager(activity)
    }

    override fun onItemClick(item: String?) {
        //startActivity(Intent(activity, WebActivity::class.java).putExtra("link", item))

        val url = URL(item)
        val fname = url.file

        val file = Utils.copyFromUrlToExternal(activity!!, item, fname, "Pivot")
        val type = file.extension

        var mimetype = when(type) {
            "jpe" -> "image/pjpeg"
            "jpeg"-> "image/jpeg"
            "pdf" -> "application/pdf"
            else -> "application/pdf"
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)

        intent.setDataAndType(Uri.fromFile(file), mimetype)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        //
        // Optionally can control visual appearance
        //
        intent.putExtra("page", "1") // Open a specific page
        intent.putExtra("zoom", "1") // Open at a specific zoom level

        startActivityForResult(intent, DOCUMENT_REQUEST_CODE)
    }

    //override fun OnEventsField(response: EventQuery.AsEventField?) {
//        activity?.runOnUiThread {
//            val adapter = EventsAttachmentAdapter(activity!!, response?.attachments(), this)
//            mRecyclerViewAttachment.adapter = adapter
//            if (response?.attachments()?.size == 0) {
//                emptyView.visibility = View.VISIBLE
//                mRecyclerViewAttachment.visibility = View.GONE
//            } else {
//                emptyView.visibility = View.GONE
//                mRecyclerViewAttachment.visibility = View.VISIBLE
//            }
//        }

    //}


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AssetAttachmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                EventAttachmentFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)

                    }
                }
    }
}// Required empty public constructor


