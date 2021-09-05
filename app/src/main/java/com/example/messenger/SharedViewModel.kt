package com.example.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.model.UserProfile
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class SharedViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _usersQueryState = MutableLiveData<DataState<Query>>()
    val usersQueryState: LiveData<DataState<Query>> get() = _usersQueryState

    private var _friendAdditionState = MutableLiveData<DataState<Int>>()
    val friendAdditionState: LiveData<DataState<Int>> get() = _friendAdditionState

    private var _currentUserProfileState = MutableLiveData<DataState<UserProfile?>>()
    val currentUserProfileState: LiveData<DataState<UserProfile?>> get() = _currentUserProfileState

    private var _friendUidState = MutableLiveData<DataState<String>>()
    val friendUidState: LiveData<DataState<String>> get() = _friendUidState

    fun signOut() = repo.signOut()


    fun defaultUserQuery() {
        viewModelScope.launch {
            repo.defaultUserQuery().collect {
                _usersQueryState.value = it
            }
        }
    }

    fun getDefaultUserQuery() =
        repo.getDefaultUserQuery()

    fun filterUserQuery(name: String?) {
        if (!name.isNullOrEmpty()) {
            viewModelScope.launch {
                repo.filterUserQuery(name).collect {
                    _usersQueryState.value = it
                }
            }
        }
    }

    fun addFriendByEmail(email: String) {
        viewModelScope.launch {
            repo.addToFriendList(Constants.USER_COLLECTION, email).collect {
                _friendAdditionState.value = it
            }
        }

    }

    fun getDocRef(collectionName: String, documentName: String) =
        repo.getDocRef(collectionName, documentName)

    @ExperimentalCoroutinesApi
    fun uIdByEmail(friendEmail: String) {
        viewModelScope.launch {
            repo.uIdByEmail(friendEmail).collect {
                _friendUidState.value = it
            }

        }

    }

    fun getCurrentUser() =
        repo.getCurrentUser()

    fun getCurrentUserProfile() {
        viewModelScope.launch {
            repo.userProfileByUId(repo.getAuth().currentUser?.uid!!).collect {
                _currentUserProfileState.value = it
            }

        }
    }

    fun onDoneNavigatingToChatsFragment() {
        _friendUidState.value = DataState.Empty
    }

    fun getDefaultMessageQuery(friendUid: String): Query = repo.getDefaultMessageQuery(friendUid)
}
