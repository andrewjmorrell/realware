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
import com.pivot.pivot360.pivoteye.Constants.TASK_CARD_TYPE_COUNTDOWN
import com.pivot.pivot360.pivoteye.Constants.TASK_CARD_TYPE_STOPWATCH
import com.pivot.pivot360.pivotglass.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.pivot.pivot360.pivotglass.BuildConfig

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

fun String.isTimer(): Boolean {
    return this == TASK_CARD_TYPE_COUNTDOWN || this == TASK_CARD_TYPE_STOPWATCH

}

fun String?.nullToNumber(): String {
    return if (this.isNullOrEmpty() or this.equals("null", true)) {
        "0"
    } else {
        this.toString()
    }
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

fun String.valueToBoolean(): Boolean {
    return when (this) {
        "no" -> {
            false
        }
        "yes" -> {
            true
        }
        else -> false
    }
}

fun Boolean.valueToString(): String {
    return when (this) {
        true -> {
            "yes"
        }
        false -> {
            "no"
        }

    }
}

fun String?.notNull(): String {
    return if (this.isNullOrEmpty() or this.equals("null", true)) {
        ""
    } else {
        this.toString()
    }
}

fun String.timeFormat(): String {
    var temp: String = if (this.isEmpty()) {
        "0"
    } else {
        this
    }
    val min = temp.toInt() / 60000 % 60
    val sec = temp.toInt() / 1000 % 60
    var ms = temp.toInt() % 1000 / 10

    if (ms > 100) ms = 0

    return (if (min > 9) min else "0$min").toString() + ":" + (if (sec > 9) sec else "0$sec") + "." + if (ms > 9) ms else "0$ms"

}

fun String.timeFormatToLong(): Long {
    val time = this.split(":")//min * 60000 + sec*1000 + ms*10
    val min = time[0].toInt()
    val seconds = time[1].split(".")
    val sec = seconds[0].toInt()
    val ms = seconds[1].toInt()
    return min * 6000 + sec * 1000 + ms * 10.toLong()

}

fun String.flavour(): String {
    return if (BuildConfig.FLAVOR.contains("pae")) this else this//Constants.BASE_URL + this
}