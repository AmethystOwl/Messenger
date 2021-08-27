package com.example.messenger

data class Message(
    val headerText: String,
    val bodyText: String?,
    val pictureUrl: String?,
    val time: String,
    // New msg? how many? etc..
    // TODO : val status: String?
    // Sent, Received, Seen, Error sending?
    // TODO : val state: String?
) {
    constructor() : this("Empty", "Empty", "Null", "00:00")
}
