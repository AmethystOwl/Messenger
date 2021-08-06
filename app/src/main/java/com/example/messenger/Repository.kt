package com.example.messenger

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
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

    @ExperimentalCoroutinesApi
    suspend fun login(email: String, password: String) = callbackFlow<DataState<Int>> {
        offer(DataState.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signInTask ->
            when {
                signInTask.isSuccessful -> {
                    offer(DataState.Success(Constants.LOGIN_SUCCESSFUL))
                }
                signInTask.isCanceled -> {
                    offer(DataState.Canceled)

                }
                signInTask.exception != null -> {
                    when (signInTask.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            offer(DataState.Invalid(Constants.LOGIN_INVALID_CREDENTIALS))

                        }
                        is FirebaseAuthInvalidUserException -> {
                            offer(DataState.Invalid(Constants.LOGIN_NO_USER))

                        }
                        else -> {
                            offer(DataState.Error(signInTask.exception!!))
                        }
                    }
                }
            }

        }
        awaitClose()

    }.flowOn(Dispatchers.IO)


    @ExperimentalCoroutinesApi
    suspend fun getCurrentUserProfile(uId: String) = callbackFlow<DataState<UserProfile?>> {
        fireStore.collection(Constants.USER_COLLECTION).document(uId).get()
            .addOnCompleteListener { documentSnapshot ->
                offer(DataState.Loading)
                when {
                    documentSnapshot.isSuccessful -> {
                        val profile = documentSnapshot.result?.toObject(UserProfile::class.java)
                        offer(DataState.Success(profile))
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
    }.flowOn(Dispatchers.IO)

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
    }.flowOn(Dispatchers.IO)

    fun getDocRef(collectionName: String, documentName: String) =
        fireStore.collection(collectionName).document(documentName)

    @ExperimentalCoroutinesApi
    fun uploadImg(uri: Uri, uId: String) = callbackFlow<DataState<Int>> {

        offer(DataState.Loading)
        val fileRef = storage.reference.child(Constants.STORAGE_IMAGES_FOLDER).child(uId)
        val uploadTask = fileRef.putFile(uri)
        uploadTask.addOnProgressListener {
            offer(DataState.Loading)
        }
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.addOnCompleteListener { downloadUri ->
                    if (downloadUri.isSuccessful) {
                        val docRef = fireStore.collection(Constants.USER_COLLECTION).document(uId)
                        docRef.get().addOnCompleteListener {
                            docRef.update(
                                Constants.FIELD_PROFILE_PICTURE_URL,
                                downloadUri.result.toString()
                            )
                            docRef.update(
                                Constants.FIELD_PROFILE_CREATION_COMPLETED,
                                true
                            )
                            offer(DataState.Success(Constants.IMAGE_UPLOAD_SUCCESSFUL))
                        }
                    } else if (downloadUri.isCanceled) {
                        offer(DataState.Canceled)
                    } else if (downloadUri.exception != null) {
                        offer(DataState.Error(downloadUri.exception!!))
                    }

                }

            } else if (task.isCanceled) {
                offer(DataState.Canceled)
            } else if (task.exception != null) {
                offer(DataState.Error(task.exception!!))
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)
}