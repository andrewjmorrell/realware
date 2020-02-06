package com.pivot.pivot360.pivoteye.asset

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pivot.pivot360.content.graphql.AssetsQuery
import com.pivot.pivot360.content.listeners.OnItemClickListener
import com.pivot.pivot360.model.AssetParameter
import com.pivot.pivot360.model.PaeAssetParameter
import com.pivot.pivot360.pivoteye.flavour
import com.pivot.pivot360.pivoteye.notNull
import com.pivot.pivot360.pivotglass.BuildConfig
import com.pivot.pivot360.pivotglass.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_asset.view.*

class AssetsAdapter(context: AppCompatActivity, assets: MutableList<AssetsQuery.Asset>, var listener: AssetsActivity) : RecyclerView.Adapter<AssetsAdapter.ViewHolder>() {
    var mContext: Context = context
    private var listData: MutableList<AssetsQuery.Asset> = assets
    var mListener: OnItemClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_asset, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(listData[position]) {
            holder.itemView.apply {
                if (BuildConfig.FLAVOR.contains("pae")) {
                    try {
                        var parameters = Gson().fromJson(parameters().toString(), PaeAssetParameter::class.java)
                        ////////////////////////////////////////////////////////////////////////

                        var _tempMap = linkedMapOf<String, String>()
                        // a.put("Type",parameters.type)
                        _tempMap["LAST FLOWN: "] = parameters.lastFlown
                        _tempMap["MC STATUS: "] = parameters.mcStatus
                        _tempMap["ECD: "] = parameters.ecd
                        _tempMap["LN-260: "] = parameters.ln260
                        _tempMap["RADAR TYPE:"] = parameters.radarType
                        _tempMap["JAMMER: "] = parameters.jammer
                        _tempMap["STATUS NOTES: "] = parameters.statusNotes

                        // a.put("Type",parameters.type)
                        var finalMap = _tempMap.filterNot { it.value == "" }
                        txtTitle.text = "${name()}"
                        txtModelMake.text = "${finalMap.keys.toList()[0]} ${finalMap.values.toList()[0]}"//TYPE : ${parameters.type}"
                        txtBarcode.text = "${finalMap.keys.toList()[1]} ${finalMap.values.toList()[1]}"
                        txtPaeRadarType.text = "${finalMap.keys.toList()[2]} ${finalMap.values.toList()[2]}"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        txtPaeRadarType.text = ""
                    }
                    //////////////////////////////////////////////////////////////////////

                    txtPaeRadarType.visibility = View.VISIBLE
                    txtDate.visibility = View.GONE

                } else {
                    txtPaeRadarType.visibility = View.GONE
                    txtDate.visibility = View.VISIBLE
                    var parameters = Gson().fromJson(parameters().toString(), AssetParameter::class.java)

                    txtTitle.text = name()
                    txtModelMake.text = description().notNull() //mContext.getString(R.string.make_model, parameters.make, parameters.model)
                    txtBarcode.text = ""//mContext.getString(R.string.barcode_assets, parameters.barcode)
                    txtDate.text = ""//parameters.age


                }
                Picasso.with(mContext).load(image().toString().flavour()).into(holder.itemView.iv_cover)
                holder.itemView.setOnClickListener {
                    listener.onItemClick(identity())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
