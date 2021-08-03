package com.example.messenger.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.example.messenger.UserProfile
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    private var _currentUserProfile = MutableLiveData<DataState<UserProfile?>>()
    val currentUserProfile: LiveData<DataState<UserProfile?>> get() = _currentUserProfile


    init {

        viewModelScope.launch {
            _currentUser.value = repo.getCurrentUser()
            if (currentUser.value != null) {
                val uId = currentUser.value?.uid
                if (uId != null) {
                    repo.getCurrentUserProfile(uId)
                        .collect(object : FlowCollector<DataState<UserProfile?>> {
                            override suspend fun emit(value: DataState<UserProfile?>) {
                                _currentUserProfile.value = value
                            }
                        })

                }
            }
        }

    }

    fun getAuth() = repo.getAuth()
    fun login() {
        viewModelScope.launch {
            repo
        }
    }
}