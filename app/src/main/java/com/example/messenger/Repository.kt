package com.example.messenger

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) {
    private val TAG = "Repository"


    @ExperimentalCoroutinesApi
    suspend fun register(userProfile: UserProfile, password: String) =
        callbackFlow<DataState<UserProfile>?> {
            offer(DataState.Loading)
            auth.createUserWithEmailAndPassword(userProfile.email!!, password)
                .addOnCompleteListener {
                    if (it.isComplete) {
                        Log.i(TAG, "register: isComplete")
                        when {
                            it.isSuccessful -> {
                                Log.i(TAG, "register: isSuccessful")

                                //  TODO("create profile before sending success state.. after after successful creation of profile.")
                                val doc = fireStore.collection(Constants.USER_COLLECTION)
                                    .document(auth.currentUser?.uid!!)
                                doc.set(userProfile).addOnCompleteListener { docTask ->
                                    when {
                                        docTask.isSuccessful -> {
                                            Log.i(TAG, "register doc: isSuccessful")
                                            offer(DataState.Success(userProfile))

                                        }
                                        docTask.isCanceled -> {
                                            Log.i(TAG, "register doc: isCanceled")
                                            offer(DataState.Canceled)

                                        }
                                        docTask.exception != null -> {
                                            Log.i(TAG, "register doc: exception")
                                            offer(DataState.Error(it.exception!!))
                                        }
                                    }
                                }
                            }
                            it.isCanceled -> {
                                Log.i(TAG, "register: isCanceled")
                                offer(DataState.Canceled)
                            }
                            it.exception != null -> {
                                Log.i(TAG, "register: exception")
                                offer(DataState.Error(it.exception!!))
                            }
                        }
                    }
                }
            awaitClose()
        }.flowOn(Dispatchers.IO)


    fun getCurrentUser(): FirebaseUser? {
        auth.currentUser?.reload()
        return auth.currentUser
    }

    suspend fun login(email: String, password: String) {
        TODO("implement")
    }

    @ExperimentalCoroutinesApi
    suspend fun getCurrentUserProfile(uId: String) = callbackFlow<DataState<UserProfile?>> {
        fireStore.collection(Constants.USER_COLLECTION).document(uId).get()
            .addOnCompleteListener { documentSnapshot ->
                offer(DataState.Loading)
                when {
                    documentSnapshot.isSuccessful -> {
                        val profile = documentSnapshot.result?.toObject(UserProfile::class.java)
                        this@callbackFlow.offer(DataState.Success(profile))
                    }
                    documentSnapshot.isCanceled -> {
                        offer(DataState.Canceled)
                    }
                    documentSnapshot.exception != null -> {
                        offer(DataState.Error(documentSnapshot.exception!!))
                    }
                }
            }
        awaitClose()
    }

    fun getAuth() = auth

    @ExperimentalCoroutinesApi
    suspend fun sendVerificationEmail(currentUser: FirebaseUser?) = callbackFlow<DataState<Int>> {
        currentUser?.let {
            currentUser.sendEmailVerification().addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        offer(DataState.Success(Constants.VERIFICATION_EMAIL_SENT_SUCCESS))
                    }
                    it.exception != null -> {
                        offer(DataState.Error(it.exception!!))
                    }
                }
            }
        }
        awaitClose()
    }

    fun getDocRef(collectionName: String, documentName: String) =
        fireStore.collection(collectionName).document(documentName)


}