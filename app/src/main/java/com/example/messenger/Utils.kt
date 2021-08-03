package com.example.messenger

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

open class Utils {
    companion object {


        fun Context.snackBar(view: View, text: String) {
            Snackbar.make(this, view, text, Snackbar.LENGTH_LONG).show()

        }

        fun getTime(): String {
            val tz = TimeZone.getTimeZone("GMT")
            val df = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z", Locale.US)
            df.timeZone = tz // strip timezone
            return df.format(Date())
        }
    }
}