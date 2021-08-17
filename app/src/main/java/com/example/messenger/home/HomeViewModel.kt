package com.example.messenger.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _usersQueryState = MutableLiveData<DataState<Query>>()
    val usersQueryState: LiveData<DataState<Query>> get() = _usersQueryState

    private var _messagesQueryState = MutableLiveData<DataState<Query>>()
    val messagesQueryState: LiveData<DataState<Query>> get() = _messagesQueryState

    private var _friendAdditionState = MutableLiveData<DataState<Int>>()
    val friendAdditionState: LiveData<DataState<Int>> get() = _friendAdditionState

    fun signOut() = repo.signOut()


    fun getDefaultMessageQuery() =
        repo.getDefaultMessageQuery()

    fun defaultMessageQuery() {
        viewModelScope.launch {
            repo.defaultMessageQuery().collect {
                _messagesQueryState.value = it
            }
        }
    }

    fun defaultUserQuery() {
        viewModelScope.launch {
            repo.defaultUserQuery().collect {
                _usersQueryState.value = it
            }
        }
    }

    fun getDefaultUserQuery() =
        repo.getDefaultUserQuery()

    @ExperimentalCoroutinesApi
    fun filterUserQuery(name: String?) {
        if (!name.isNullOrEmpty()) {
            viewModelScope.launch {
                repo.filterUserQuery(name).collect {
                    _usersQueryState.value = it
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun addFriendByEmail(email: String) {
        viewModelScope.launch {
            repo.addToFriendList(Constants.USER_COLLECTION, email).collect {
                _friendAdditionState.value = it
            }

        }
    }


}