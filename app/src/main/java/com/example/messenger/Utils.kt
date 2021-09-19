package com.example.messenger

import android.os.Build
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

open class Utils {


    companion object {
        private fun getMonth(index: Int): String {
            val monthArray = ArrayList<String>()
            monthArray.add("Jan")
            monthArray.add("Feb")
            monthArray.add("Mar")
            monthArray.add("Apr")
            monthArray.add("May")
            monthArray.add("Jun")
            monthArray.add("Jul")
            monthArray.add("Sep")
            monthArray.add("Oct")
            monthArray.add("Nov")
            monthArray.add("Dec")
            return monthArray[index]
        }

        fun View.showSnackbar(
            view: View,
            msg: String,
            length: Int,
            actionMessage: CharSequence?,
            action: ((View) -> Unit)?
        ) {
            val snackbar = Snackbar.make(view, msg, length)
            if (actionMessage != null) {
                snackbar.setAction(actionMessage) {
                    action?.invoke(this)
                }.show()
            } else {
                snackbar.show()
            }
        }

        fun getTime(): String {
            val tz = TimeZone.getTimeZone("UTC")
            val df = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z", Locale.US)
            df.timeZone = tz
            return df.format(Date())
        }


        fun getTimeStringFromTimeStamp(timeStampInMillis: Long): String {

            val calender = Calendar.getInstance()
            calender.timeInMillis = timeStampInMillis

            return calender.get(Calendar.HOUR).toString() + ':' + calender.get(Calendar.MINUTE)
                .toString() + ' ' + getAMorPM(calender.get(Calendar.AM_PM))
        }

        private fun getAMorPM(index: Int): String {
            return when {
                index > 0 -> "PM"
                else -> "AM"
            }
        }

        // create array of months strings, months[ calender.get(Calender.MONTH) ] format : Jan 12, 2021
        fun getDateStringFromTimeStamp(timeStampInMillis: Long): String {
            val calender = Calendar.getInstance()
            calender.timeInMillis = timeStampInMillis

            return getMonth(
                calender.get(
                    Calendar.MONTH
                ) + 1
            ) + ' ' +
                    calender.get(Calendar.DAY_OF_MONTH).toString() + ", " +
                    calender.get(Calendar.YEAR).toString()
        }


        inline fun <T> isSdkVer29Up(onSdk29Up: () -> T): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                onSdk29Up()
            } else null
        }
    }
}