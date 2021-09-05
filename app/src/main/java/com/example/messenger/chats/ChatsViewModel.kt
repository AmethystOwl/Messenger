package com.example.messenger.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class ChatsViewModel @Inject constructor(private val repo: Repository) : ViewModel() {


    private var _messagesQueryState = MutableLiveData<DataState<Query>>()
    val messagesQueryState: LiveData<DataState<Query>> get() = _messagesQueryState


/*    fun getDefaultMessageQuery() =
        repo.getDefaultMessageQuery()*/

    fun defaultMessageQuery() {
        viewModelScope.launch {
            repo.defaultMessageQuery().collect {
                _messagesQueryState.value = it
            }
        }
    }

}