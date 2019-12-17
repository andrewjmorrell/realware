package com.pivot.pivot360.pivoteye

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pivot.pivot360.pivotglass.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show()
}

fun Activity.startEmptyActivity(Other: Activity) {
    startActivity(Intent(this, Other::class.java))
}

fun Fragment.startEmptyActivity(Other: Activity) {
    startActivity(Intent(this.activity, Other::class.java))
}

fun Fragment.startActivityWithData(Other: Activity, bundle: Bundle) {
    startActivity(Intent(this.activity, Other::class.java).putExtra("data", bundle))
}

fun Activity.startActivityWithData(Other: Activity, bundle: Bundle) {
    startActivity(Intent(this, Other::class.java).putExtra("data", bundle))
}

fun Activity.runLayoutAnimation(recyclerView: RecyclerView) {
    val controller =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)

    recyclerView.layoutAnimation = controller
    recyclerView.adapter?.notifyDataSetChanged()
    recyclerView.scheduleLayoutAnimation()
}

fun String.convertUTCToLocal(dateFormatInPut: String, dateFormatOutPut: String): String {


    var dateToReturn = this

    val sdf = SimpleDateFormat(dateFormatInPut)
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    var dateLocal: Date? = null

    val sdfOutPutToSend = SimpleDateFormat(dateFormatOutPut)
    sdfOutPutToSend.timeZone = TimeZone.getDefault()

    try {

        dateLocal = sdf.parse(this)
        dateToReturn = sdfOutPutToSend.format(dateLocal)

    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return dateToReturn
}

fun String.fromHtml(): Spanned {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }

}