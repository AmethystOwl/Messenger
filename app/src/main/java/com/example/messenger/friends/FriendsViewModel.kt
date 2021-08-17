package com.example.messenger.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.example.messenger.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class FriendsViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _friendsList = MutableLiveData<DataState<List<UserProfile>>>()
    val friendsList: LiveData<DataState<List<UserProfile>>> get() = _friendsList

    init {
        getFriendList()
    }

    private fun getFriendList() =
        viewModelScope.launch {
            repo.defaultFriendsQuery().collect {
                _friendsList.value = it
            }
        }


}
