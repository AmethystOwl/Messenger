package com.example.messenger.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.Repository
import com.example.messenger.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
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

    private var _loginState = MutableLiveData<DataState<Int>>()
    val loginState: LiveData<DataState<Int>> get() = _loginState
    private var _errorString = MutableLiveData<String?>()
    val errorString: LiveData<String?> get() = _errorString


    init {
        viewModelScope.launch {

        }

    }

    fun getAuth() = repo.getAuth()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            repo.login(email, password).collect {
                _loginState.value = it
            }
        }
    }

    fun isValidEmail(email: String): Int {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Constants.INVALID_EMAIL_FORMAT
        } else {
            Constants.VALID_EMAIL
        }
    }


    fun isValidPassword(password: String): Int {
        return if (password.length < 8) {
            Constants.SHORT_PASSWORD
        } else {
            Constants.VALID_PASSWORD
        }
    }

    fun setError(errStr: String?) {
        _errorString.value = errStr
    }

    fun refreshCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repo.getCurrentUser()
            if (currentUser.value != null) {
                val uId = currentUser.value?.uid
                if (uId != null) {
                    repo.userProfileByUId(uId).collect {
                        _currentUserProfile.value = it

                    }

                }
            }
        }

    }
}