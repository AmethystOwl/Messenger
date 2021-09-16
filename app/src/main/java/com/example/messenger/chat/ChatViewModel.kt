package com.example.messenger.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.example.messenger.model.Message
import com.example.messenger.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class ChatViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    private var _friendUserInfoState = MutableLiveData<DataState<UserProfile?>>()
    val friendUserDataState: LiveData<DataState<UserProfile?>> get() = _friendUserInfoState

    private var _friendUserInfo = MutableLiveData<UserProfile?>()
    val friendUserData: LiveData<UserProfile?> get() = _friendUserInfo

    private var _messageState = MutableLiveData<DataState<Message?>>()
    val messageState: LiveData<DataState<Message?>> get() = _messageState

    private var _imageMessageState = MutableLiveData<DataState<Int?>>()
    val imageMessageState: LiveData<DataState<Int?>> get() = _imageMessageState

    private var _selectedMessage = MutableLiveData<Message?>()
    val selectedMessage: LiveData<Message?> get() = _selectedMessage

    private var _selectedMessagePosition = MutableLiveData<Int?>()
    val selectedMessagePosition: LiveData<Int?> get() = _selectedMessagePosition

    private var _documentChanges = MutableLiveData<DataState<Int?>>()
    val documentChanges: LiveData<DataState<Int?>> get() = _documentChanges

    init {
        _selectedMessagePosition.value = 0
    }

    fun friendInfo(uId: String) {
        viewModelScope.launch {
            repo.userProfileByUId(uId).collect {
                _friendUserInfoState.value = it
            }
        }

    }

    fun setFriendProfile(profile: UserProfile) {
        _friendUserInfo.value = profile
    }

    fun sendMessage(message: Message, toUid: String) {
        viewModelScope.launch {
            repo.sendMessage(message, toUid).collect {
                _messageState.value = it
            }
        }
    }


    fun sendImageMessage(message: Message, imageUri: Uri, friendUid: String) {
        viewModelScope.launch {
            repo.sendImage(message, imageUri, friendUid).collect {
                _imageMessageState.value = it
            }
        }
    }

    fun setSelectedMessage(message: Message?, position: Int?) {
        _selectedMessage.value = message
        _selectedMessagePosition.value = position
    }

    fun observeDocChanges(friendUid: String) {
        viewModelScope.launch {
            repo.observeDocChanges(friendUid).collect {
                _documentChanges.value = it
            }
        }
    }
}