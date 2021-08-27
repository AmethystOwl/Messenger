package com.example.messenger.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.DataState
import com.example.messenger.Repository
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
}