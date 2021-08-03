package com.example.messenger.completeProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor() : ViewModel() {

    private var _currentProfile = MutableLiveData<UserProfile?>()
    val currentProfile: LiveData<UserProfile?> get() = _currentProfile


}