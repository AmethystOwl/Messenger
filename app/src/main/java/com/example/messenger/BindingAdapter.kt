package com.example.messenger

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.messenger.model.Message


class BindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["verifyFirstName", "verifyLastName"], requireAll = false)
        fun TextView.verifyEmail(verifyFirstName: String?, verifyLastName: String?) {
            text = when {
                verifyFirstName != null && verifyLastName != null -> {
                    "Hello $verifyFirstName $verifyLastName\nPlease verify your Email Address"
                }

                else -> {
                    ""
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["firstName", "lastName"], requireAll = false)
        fun TextView.showName(firstName: String?, lastName: String?) {
            text = when {
                firstName != null && lastName != null -> {
                    "$firstName $lastName"
                }

                else -> {
                    "Unknown"
                }
            }
        }

        @JvmStatic
        @BindingAdapter("completeFirstName")
        fun TextView.completeProfile(completeFirstName: String?) {
            text = when {
                completeFirstName != null -> {
                    "$completeFirstName\nPlease complete your profile by adding a profile picture"
                }
                else -> {
                    ""
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bindChatProfilePicture")
        fun ImageView.bindChatProfilePicture(url: String?) {
            when {
                url != null -> {
                    Glide.with(context)
                        .load(url)
                        .override(400, 400)
                        .into(this)
                }
                else -> {
                    Glide.with(context)
                        .load(R.drawable.avatar)
                        .override(400, 400)
                        .into(this)

                }
            }
        }


        @JvmStatic
        @BindingAdapter("bindImageMessage")
        fun ImageView.bindImageMessage(uri: String?) {
            uri?.let {
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.ic_baseline_thumbnail_image_200)
                    .error(R.drawable.ic_baseline_broken_image_200)
                    .override(512, 512)
                    .into(this)
            }

        }

        @JvmStatic
        @BindingAdapter("bindImage")
        fun ImageView.bindImage(imageUrl: String?) {
            imageUrl?.let {
                Glide.with(context)
                    .load(imageUrl)
                    .into(this)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["isChecked", "isSender"], requireAll = true)
        fun CardView.setCardCheckedBk(isChecked: Boolean, isSender: Boolean) {
            setCardBackgroundColor(
                when (isSender) {
                    true -> {
                        when (isChecked) {
                            true -> Color.rgb(153, 51, 255)
                            false -> Color.rgb(255, 102, 255)
                        }
                    }
                    false -> {
                        when (isChecked) {
                            true -> Color.DKGRAY
                            false -> Color.GRAY
                        }
                    }
                }

            )

        }

        @JvmStatic
        @BindingAdapter("messageTimeVisibility")
        fun TextView.messageTimeVisibility(isVisible: Boolean) {
            visibility = when (isVisible) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("setRecordingDuration")
        fun TextView.setRecordingDuration(duration: Long) {
            when (duration) {
                0L -> {
                    text = resources.getText(R.string.no_duration)

                }
                else -> {
                    val minutes = duration / 1000 / 60
                    val seconds = duration / 1000 % 60
                    var sMinutes = minutes.toString()
                    var sSeconds = seconds.toString()
                    if (minutes < 10) {
                        sMinutes = "0$sMinutes"
                    }
                    if (seconds < 10) {
                        sSeconds = "0$sSeconds"
                    }
                    text = "$sMinutes:$sSeconds"

                }

            }

        }

        @JvmStatic
        @BindingAdapter("setRecordingProgress")
        fun TextView.setRecordingProgress(progress: String?) {
            text = when (progress) {
                null -> "00:00"
                else -> progress
            }
        }

        @JvmStatic
        @BindingAdapter("setProgressBarMax")
        fun ProgressBar.setProgressBarMax(value: Long?) {

            max = when (value) {
                null -> 0
                0L -> 0
                else -> {
                    value.toInt() - 100        // workaround to let progressbar fill to end
                }

            }


        }

        @JvmStatic
        @BindingAdapter("setProgressBarInterpolator")
        fun ProgressBar.setProgressBarInterpolator(message: Message?) {
            message?.voiceMessageLengthMillis?.let {
                // fix this...
                max = message.voiceMessageLengthMillis?.toInt()!!
                val animation = ObjectAnimator.ofInt(this, "progress", 0, this.max)
                animation.duration = 500
                animation.setAutoCancel(true)
                animation.interpolator = LinearInterpolator()
                animation.start()
            }


        }

        @JvmStatic
        @BindingAdapter("setRecordingProgressBar")
        fun ProgressBar.setRecordingProgressBar(value: Int?) {
            progress = when (value) {
                null -> 0
                0 -> 0
                else -> value/* + 500*/
            }
        }

        @JvmStatic
        @BindingAdapter("setMessageStatus")
        fun ImageView.setMessageStatus(status: Boolean) {
            when (status) {
                true -> {
                    Glide.with(context)
                        .load(R.drawable.ic_baseline_check_circle_outline_white_15)
                        .into(this)
                }
                false -> {
                    Glide.with(context)
                        .load(R.drawable.ic_baseline_access_time_white_15)
                        .into(this)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bindHeader")
        fun TextView.bindHeader(textString: String?) {
            text = textString ?: "Unknown"
        }

        @JvmStatic
        @BindingAdapter("bindCountry")
        fun TextView.bindCountry(textString: String?) {
            text = textString ?: "Unknown"
        }

        @JvmStatic
        @BindingAdapter("bindLastMsg")
        fun TextView.bindLastMsg(textString: String?) {
            text = textString ?: "Start the conversation by saying Hi!"
        }


        @JvmStatic
        @BindingAdapter("bindDate")
        fun TextView.bindDate(textString: String?) {
            text = textString ?: "Undefined"
        }


        @JvmStatic
        @BindingAdapter("getDateFromTimeStamp")
        fun TextView.getDateFromTimeStamp(timeStamp: Long) {
            text = Utils.getDateStringFromTimeStamp(timeStamp)
        }


        @JvmStatic
        @BindingAdapter("getTimeFromTimeStamp")
        fun TextView.getTimeFromTimeStamp(timeStamp: Long) {
            text = Utils.getTimeStringFromTimeStamp(timeStamp)
        }

        @JvmStatic
        @BindingAdapter("bindMessage")
        fun TextView.bindMessage(message: String?) {
            message?.let {
                text = message
            }

        }


        @JvmStatic
        @BindingAdapter(
            value = ["selectedMessage", "senderFirstName", "senderLastName"],
            requireAll = true
        )
        fun TextView.bindSelectedMessageName(
            selectedMessage: Message?,
            senderFirstName: String?,
            senderLastName: String?
        ) {
            when (selectedMessage?.isSender) {
                true -> {
                    text = context.resources.getString(R.string.you)
                }
                false -> {
                    text = "$senderFirstName $senderLastName"
                }
            }

        }

        @JvmStatic
        @BindingAdapter("bindSelectedMessageImage")
        fun ImageView.bindSelectedMessageImage(message: Message?) {
            message?.imageMessageUrl?.let {
                Glide.with(context)
                    .load(message.imageMessageUrl)
                    .into(this)
            }

        }

    }
}


