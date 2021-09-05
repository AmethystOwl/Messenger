package com.example.messenger.model

import com.google.firebase.firestore.Exclude

data class Message(
    var message: String? = null,
    var imageMessageUrl: String? = null,
    var senderUid: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var profilePictureUrl: String? = null,
    @get:Exclude var isSent: Boolean = false
)

