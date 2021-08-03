package com.example.messenger

import android.os.Parcelable
import com.example.messenger.Constants.STATUS_ONLINE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
    val accountCreationDate: String? = Utils.getTime(),
    var fname: String?,
    var lname: String?,
    var email: String?,
    var country: String?,
    var dateOfBirth: String?,
    var phoneNumber: String? = null,
    var isPhoneVerified: Boolean? = false,
    var profilePictureUrl: String? = null,
    var friendsCount: Int? = 0,
    var status: String? = STATUS_ONLINE,
    var allowDirectMessaging: Boolean? = true,
    var isProfileCreationCompleted: Boolean? = false
) : Parcelable {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}
