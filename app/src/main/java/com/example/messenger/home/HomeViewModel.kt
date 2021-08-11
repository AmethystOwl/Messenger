package com.example.messenger.home

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
class HomeViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _queryState = MutableLiveData<DataState<Query>>()
    val queryState: LiveData<DataState<Query>> get() = _queryState


    fun signOut() = repo.signOut()


    fun defaultUserQuery() {
        viewModelScope.launch {
            repo.defaultUserQuery().collect {
                _queryState.value = it
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun filterUserQuery(name: String?) {
        if (!name.isNullOrEmpty()) {
            viewModelScope.launch {
                repo.filterUserQuery(name).collect {
                    _queryState.value = it
                }
            }
        }
    }

    fun getDefaultUserQuery(): Query = repo.getDefaultUserQuery()


}