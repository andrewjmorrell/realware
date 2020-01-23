package com.pivot.pivot360.pivoteye.asset

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pivot.pivot360.content.graphql.AssetsQuery
import com.pivot.pivot360.pivoteye.Constants
import com.pivot.pivot360.pivoteye.Constants.IDENTITY
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.item_asset.view.*

class AssetsAdapter(var mChatRepo: MutableList<AssetsQuery.Asset>, var mContext: AssetsActivity) : RecyclerView.Adapter<AssetsAdapter.ViewHolder>() {
    var title: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_asset, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        with(mChatRepo[position]) {
//            holder.itemView.apply {
//
//
//                txtTitle.text = name()
//                txtModelMake.text = mContext.getString(R.string.make_model, parameters()?.make(), parameters()?.model())
//                txtBarcode.text = mContext.getString(R.string.barcode_assets, parameters()?.barcode())
//                txtDate.text = parameters()?.age()
//                Glide.with(mContext).load(Constants.BASE_URL + image()).into(holder.itemView.iv_cover)
//                holder.itemView.setOnClickListener {
//                    mContext.startActivity(Intent(mContext, AssetActivity::class.java).putExtra(IDENTITY, identity()))
//                }
//            }
//        }
    }

    override fun getItemCount(): Int {
        return mChatRepo.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
