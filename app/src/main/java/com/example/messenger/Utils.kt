package com.example.messenger

import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

open class Utils {
    companion object {
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
            val tz = TimeZone.getTimeZone("GMT")
            val df = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z", Locale.US)
            df.timeZone = tz // strip timezone
            return df.format(Date())
        }
    }
}