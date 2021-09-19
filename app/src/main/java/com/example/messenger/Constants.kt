package com.example.messenger

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

    const val IMAGE_UPLOAD_SUCCESSFUL = 8000

    const val STATUS_ONLINE = "Online"
    const val STATUS_OFFLINE = "Offline"
    const val STATUS_AWAY = "Away"

    val USER_COLLECTION = "users"
    val MESSAGE_COLLECTION = "messages"
    val USER_INBOX_COLLECTION = "inbox"
    val USER_CONVERSATION_COLLECTION = "conversation"

    val FIELD_ACCOUNT_CREATION_DATE = "accountCreationDate"
    val FIELD_ALLOW_DIRECT_MESSAGING = "allowDirectMessaging"
    val FIELD_COUNTRY = "country"
    val FIELD_DATE_OF_BIRTH = "dateOfBirth"
    val FIELD_EMAIL = "email"
    val FIELD_FIRST_NAME = "fname"
    val FIELD_LAST_NAME = "lname"
    val FIELD_FRIENDS_COUNT = "friendsCount"
    val FIELD_FRIENDS_LIST = "friendsList"
    val FIELD_TIMESTAMP = "timestamp"
    val FIELD_TIME = "time"

    val FIELD_PHONE_NUMBER = "phoneNumber"
    val FIELD_PHONE_VERIFIED = "phoneVerified"
    val FIELD_PROFILE_CREATION_COMPLETED = "profileCreationCompleted"
    val FIELD_PROFILE_PICTURE_URL = "profilePictureUrl"
    val FIELD_STATUS = "status"
    val FIELD_SEARCH_LIST = "searchList"

    val FIELD_INBOX_SENDER_UID = "senderUid"

}