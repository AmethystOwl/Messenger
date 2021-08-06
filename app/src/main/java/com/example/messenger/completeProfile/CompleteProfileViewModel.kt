package com.example.messenger.completeProfile

import android.net.Uri
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
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private var _currentProfile = MutableLiveData<UserProfile?>()
    val currentProfile: LiveData<UserProfile?> get() = _currentProfile
    private var _uploadImgState = MutableLiveData<DataState<Int>>()
    val uploadImgState: LiveData<DataState<Int>> get() = _uploadImgState

    fun setCurrentUser(userProfile: UserProfile) {
        _currentProfile.value = userProfile
    }


    fun getAuthUser() = repo.getCurrentUser()

    fun getDocRef(collectionName: String, documentName: String) =
        repo.getDocRef(collectionName, documentName)

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    fun uploadImg(uri: Uri, uId: String) {
        viewModelScope.launch {
            repo.uploadImg(uri, uId).collect(object : FlowCollector<DataState<Int>> {
                override suspend fun emit(value: DataState<Int>) {
                    _uploadImgState.value = value
                }

            })
        }

    }

    /*fun pickPicture() {



        // get picture then call repo.upload(uri)..
    }*/

}