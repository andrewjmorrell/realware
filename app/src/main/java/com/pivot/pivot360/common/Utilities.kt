package com.pivot.pivot360.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun convertDateOpenEvent(string: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date = dateFormat.parse(string)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            formatter.format(date)
        } catch (e: Exception) {
            string
        }
    }

    fun convertDateClosedEvent(string: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date = dateFormat.parse(string)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            formatter.format(date)
        } catch (e: Exception) {
            string
        }
    }

    fun convertDateAttachments(timestamp: Long): String {
        try {
            val c = Calendar.getInstance()
            c.timeInMillis = timestamp
            val d = c.time
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a")
            return sdf.format(d)
        } catch (e: Exception) {
            return timestamp.toString()
        }
    }

    fun convertDateAttachmentsNew(datetime: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = dateFormat.parse(datetime)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            formatter.format(date)
        } catch (e: Exception) {
            datetime
        }
    }

    fun ConvertUTCToLocal(dateFormatInPut: String, dateFomratOutPut: String, datesToConvert: String): String {


        var dateToReturn = datesToConvert

        val sdf = SimpleDateFormat(dateFormatInPut)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        var dateLocal: Date? = null

        val sdfOutPutToSend = SimpleDateFormat(dateFomratOutPut)
        sdfOutPutToSend.timeZone = TimeZone.getDefault()

        try {

            dateLocal = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(dateLocal)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateToReturn
    }
}