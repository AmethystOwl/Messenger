package com.example.messenger.model

import com.google.firebase.firestore.Exclude

data class Message(
    var message: String? = null,
    var imageMessageUrl: String? = null,
    var voiceMessageUrl: String? = null,
    var voiceMessageLengthMillis: Long? = null,

    var senderUid: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var profilePictureUrl: String? = null,
    @get:Exclude var isSent: Boolean = false,
    @get:Exclude var isChecked: Boolean = false,
    @get:Exclude var isSender: Boolean = false,
    @get:Exclude var recordingProgress: String = "00:00",
    @get:Exclude var recordingProgressBar: Int = 0,
    @get:Exclude var isPlayingRecord: Boolean = false,


    )

