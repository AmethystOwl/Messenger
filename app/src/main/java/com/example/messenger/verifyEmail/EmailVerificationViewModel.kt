package com.example.messenger.verifyEmail

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.example.messenger.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class EmailVerificationViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    private val TAG = "EmailVerificationViewMo"

    private var _currentProfile = MutableLiveData<UserProfile?>()
    val currentProfile: LiveData<UserProfile?> get() = _currentProfile

    private var _emailVerificationState = MutableLiveData<DataState<Int>>()
    val emailVerificationState: LiveData<DataState<Int>> get() = _emailVerificationState

    private var _counterValue = MutableLiveData<Long>()
    val counterValue: LiveData<Long> get() = _counterValue

    private var _isCounterActivated = MutableLiveData<Boolean>()
    val isCounterActivated: LiveData<Boolean> get() = _isCounterActivated

    init {
        viewModelScope.launch {
            if (repo.getCurrentUser() != null && repo.getCurrentUser()?.isEmailVerified == false) {
                sendVerificationCode()
            } else if (repo.getCurrentUser() != null && repo.getCurrentUser()?.isEmailVerified == true) {
                _emailVerificationState.value = DataState.Success(Constants.EMAIL_VERIFIED)
            }

        }
        _isCounterActivated.value = false
    }

    fun setCurrentUser(user: UserProfile) {
        _currentProfile.value = user
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    private suspend fun sendVerificationCode() {
        repo.sendVerificationEmail(repo.getCurrentUser()).collect(object :
            FlowCollector<DataState<Int>> {
            override suspend fun emit(value: DataState<Int>) {
                _emailVerificationState.value = value
            }

        })
    }

    fun isEmailVerified(): Boolean? {
        repo.getCurrentUser()?.reload()
        return repo.getCurrentUser()?.isEmailVerified

    }

    fun resendVerificationCode() {
        viewModelScope.launch {
            sendVerificationCode()
        }
        countDown()
    }

    fun getDocRef(collectionName: String, documentName: String) =
        repo.getDocRef(collectionName, documentName)

    fun getAuthUser() = repo.getCurrentUser()


    fun countDown() {
        _isCounterActivated.value = true
        object : CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                _counterValue.postValue(p0)
                Log.i(TAG, "onTick: ${_counterValue.value}")
            }

            override fun onFinish() {
                _isCounterActivated.value = false
            }
        }.start()
    }
}
