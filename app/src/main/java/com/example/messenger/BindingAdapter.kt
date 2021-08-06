package com.example.messenger

import android.widget.TextView
import androidx.databinding.BindingAdapter

class BindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["verifyFirstName", "verifyLastName"], requireAll = false)
        fun TextView.verifyEmail(verifyFirstName: String?, verifyLastName: String?) {
            if (verifyFirstName != null && verifyLastName != null) {
                text = "Hello $verifyFirstName $verifyLastName\nPlease verify your Email Address"
            }
        }


        @JvmStatic
        @BindingAdapter("completeFirstName")
        fun TextView.completeProfile(completeFirstName: String?) {
            if (completeFirstName != null) {
                text =
                    "$completeFirstName\nPlease complete your profile by adding a profile picture"
            } else {
                text = ""
            }
        }


    }
}