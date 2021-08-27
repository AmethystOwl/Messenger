package com.example.messenger.register

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

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    private val _signUpState = MutableLiveData<DataState<UserProfile>?>()
    val signUpState: LiveData<DataState<UserProfile>?> get() = _signUpState


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

    /*  fun isValidPhoneNumber(phoneNumber: String): Int {
          val patterns =
              ("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")
          val pattern = Pattern.compile(patterns)
          return if (!pattern.matcher(phoneNumber).matches()) {
              Constants.INVALID_PHONE_NUMBER
          } else {
              Constants.VALID_PHONE_NUMBER
          }
      }*/


    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun signUp(userProfile: UserProfile, password: String) {
        viewModelScope.launch {
            repo.register(userProfile, password)
                .collect(object : FlowCollector<DataState<UserProfile>?> {
                    override suspend fun emit(value: DataState<UserProfile>?) {
                        _signUpState.value = value
                    }
                })
        }
    }

    fun doneObserving() {
        _signUpState.value = DataState.Empty
    }


    fun getCurrentUser() = repo.getCurrentUser()


}