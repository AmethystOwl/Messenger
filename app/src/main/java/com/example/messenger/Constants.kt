package com.example.messenger

import android.media.AudioFormat

object Constants {
    const val DOCUMENT_ADDED = 601
    const val DOCUMENT_MODIFIED = 602
    const val DOCUMENT_REMOVED = 603


    const val IMAGE_MESSAGE_SUCCESS = 600
    const val STORAGE_CHAT_UPLOADS_FOLDER = "uploads"
    const val INTENT_TYPE_IMAGE = "image/*"

    const val LOGIN_NO_USER = -302
    const val LOGIN_SUCCESSFUL = 301
    const val LOGIN_INVALID_CREDENTIALS = -301

    const val STORAGE_PROFILE_PICTURE_FOLDER = "ProfilePictures"


    const val VALID_EMAIL = 101
    const val INVALID_EMAIL_FORMAT = -101

    const val VALID_PASSWORD = 201
    const val SHORT_PASSWORD = -201
    const val EMAIL_VERIFIED = 501

    const val PICK_IMAGE_REQEST = 654
    const val VERIFICATION_EMAIL_SENT_SUCCESS = 901

    const val FRIEND_ADDITION_SUCCESS = 701
    const val FRIEND_ADDITION_FAIL = -701
    const val FRIEND_ADDITION_CANCEL = -702

    const val CAMERA_PERMISSION_REQ_CODE = 1001
    const val READ_EXTERNAL_PERMISSION_REQ_CODE = 1002
    const val WRITE_EXTERNAL_PERMISSION_REQ_CODE = 1003
    const val AUDIO_RECORD_PERMISSION_REQ_CODE = 1004


    const val IMAGE_UPLOAD_SUCCESSFUL = 8000

    const val STATUS_ONLINE = "Online"
    const val STATUS_OFFLINE = "Offline"
    const val STATUS_AWAY = "Away"

    const val USER_COLLECTION = "users"
    const val MESSAGE_COLLECTION = "messages"
    const val USER_INBOX_COLLECTION = "inbox"
    const val USER_CONVERSATION_COLLECTION = "conversation"

    const val FIELD_ACCOUNT_CREATION_DATE = "accountCreationDate"
    const val FIELD_ALLOW_DIRECT_MESSAGING = "allowDirectMessaging"
    const val FIELD_COUNTRY = "country"
    const val FIELD_DATE_OF_BIRTH = "dateOfBirth"
    const val FIELD_EMAIL = "email"
    const val FIELD_FIRST_NAME = "fname"
    const val FIELD_LAST_NAME = "lname"
    const val FIELD_FRIENDS_COUNT = "friendsCount"
    const val FIELD_FRIENDS_LIST = "friendsList"
    const val FIELD_TIMESTAMP = "timestamp"
    const val FIELD_TIME = "time"

    const val FIELD_PHONE_NUMBER = "phoneNumber"
    const val FIELD_PHONE_VERIFIED = "phoneVerified"
    const val FIELD_PROFILE_CREATION_COMPLETED = "profileCreationCompleted"
    const val FIELD_PROFILE_PICTURE_URL = "profilePictureUrl"
    const val FIELD_STATUS = "status"
    const val FIELD_SEARCH_LIST = "searchList"

    const val FIELD_INBOX_SENDER_UID = "senderUid"


    const val SAMPLING_RATE_IN_HZ = 44100
    const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    const val BufferElements2Rec = 1024
    const val BytesPerElement = 2
    const val BUFFER_SIZE = BufferElements2Rec * BytesPerElement
}