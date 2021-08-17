package com.example.messenger

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
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
                                if (userProfile.fname != null && userProfile.lname != null) {
                                    val fullName = userProfile.fname + " " + userProfile.lname
                                    val fullNameArr = fullName.split(' ')
                                    var holder = ""
                                    for (name in fullNameArr) {
                                        for (c in name) {
                                            holder += c
                                            userProfile.searchList.add(holder)
                                        }
                                        userProfile.searchList.add("$holder ")
                                        holder = ""
                                    }
                                    var fullNameHolder = ""
                                    for (fullNameChar in fullName) {
                                        fullNameHolder += fullNameChar
                                        userProfile.searchList.add(fullNameHolder)
                                    }
                                }
                                if (userProfile.email != null) {
                                    userProfile.searchList.add(userProfile.email!!)
                                }


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
    suspend fun addToFriendList(collectionName: String, email: String) =
        callbackFlow<DataState<Int>> {
            fireStore.collection(collectionName)
                .whereEqualTo(Constants.FIELD_EMAIL, email)
                .get()
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            if (task.result?.documents?.size == 1) {
                                task.result?.documents?.forEach { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val uId = documentSnapshot.id
                                        val currentUserId = auth.currentUser?.uid!!
                                        fireStore.collection(Constants.USER_COLLECTION)
                                            .document(currentUserId).get()
                                            .addOnCompleteListener { currentUserDocSnapShot ->
                                                when {
                                                    currentUserDocSnapShot.isSuccessful -> {
                                                        val currentUserProfile =
                                                            currentUserDocSnapShot.result?.toObject(
                                                                UserProfile::class.java
                                                            )
                                                        if (currentUserProfile != null) {
                                                            currentUserDocSnapShot.result?.reference?.update(
                                                                Constants.FIELD_FRIENDS_COUNT,
                                                                currentUserProfile.friendsCount?.plus(
                                                                    1
                                                                )
                                                            )
                                                                ?.addOnCompleteListener { friendsCountTask ->
                                                                    when {
                                                                        friendsCountTask.isSuccessful -> {
                                                                            currentUserProfile.friendsList.add(
                                                                                uId
                                                                            )
                                                                            currentUserDocSnapShot.result?.reference?.update(
                                                                                Constants.FIELD_FRIENDS_LIST,
                                                                                currentUserProfile.friendsList
                                                                            )
                                                                                ?.addOnCompleteListener { friendsListTask ->
                                                                                    when {
                                                                                        friendsListTask.isSuccessful -> {
                                                                                            offer(
                                                                                                DataState.Success(
                                                                                                    Constants.FRIEND_ADDITION_SUCCESS
                                                                                                )
                                                                                            )
                                                                                        }
                                                                                        friendsListTask.isCanceled -> {
                                                                                            offer(
                                                                                                DataState.Canceled
                                                                                            )
                                                                                        }
                                                                                        friendsListTask.exception != null -> {
                                                                                            offer(
                                                                                                DataState.Error(
                                                                                                    friendsCountTask.exception!!
                                                                                                )
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                        }
                                                                        friendsCountTask.isCanceled -> {
                                                                            offer(DataState.Canceled)
                                                                        }
                                                                        friendsCountTask.exception != null -> {
                                                                            offer(
                                                                                DataState.Error(
                                                                                    friendsCountTask.exception!!
                                                                                )
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }
                                                    currentUserDocSnapShot.isCanceled -> {
                                                        offer(DataState.Canceled)
                                                    }
                                                    currentUserDocSnapShot.exception != null -> {
                                                        offer(DataState.Error(currentUserDocSnapShot.exception!!))
                                                    }
                                                }
                                            }
                                    } else {
                                        offer(DataState.Empty)
                                    }
                                }
                            }

                        }
                        task.isCanceled -> {
                            offer(DataState.Canceled)
                        }
                        task.exception != null -> {
                            offer(DataState.Error(task.exception!!))

                        }

                    }
                }
            awaitClose()
        }.flowOn(Dispatchers.IO)


    @ExperimentalCoroutinesApi
    suspend fun uploadImg(uri: Uri, uId: String) = callbackFlow<DataState<Int>> {
        offer(DataState.Loading)
        val fileRef =
            storage.reference.child(Constants.STORAGE_PROFILE_PICTURE_FOLDER).child(uId)
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
                        val docRef =
                            fireStore.collection(Constants.USER_COLLECTION).document(uId)
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

    fun signOut() {

        auth.signOut()
    }

    fun getDefaultMessageQuery() =
        fireStore.collection(Constants.MESSAGE_COLLECTION).limit(15)

    suspend fun defaultMessageQuery() = flow<DataState<Query>> {
        emit(DataState.Loading)
        try {
            // TODO : Edit later
            val query = fireStore.collection(Constants.MESSAGE_COLLECTION).limit(15)
            emit(DataState.Success(query))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getDefaultUserQuery(): Query =
        fireStore.collection(Constants.USER_COLLECTION)
            .whereNotEqualTo(Constants.FIELD_EMAIL, auth.currentUser?.email)
            .orderBy(Constants.FIELD_EMAIL, Query.Direction.ASCENDING)

    suspend fun defaultUserQuery() = flow<DataState<Query>> {
        emit(DataState.Loading)
        try {
            val query = fireStore.collection(Constants.USER_COLLECTION)
                .whereNotEqualTo(Constants.FIELD_EMAIL, auth.currentUser?.email)
                .orderBy(Constants.FIELD_EMAIL, Query.Direction.ASCENDING)
            emit(DataState.Success(query))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun filterUserQuery(name: String) = flow<DataState<Query>> {
        emit(DataState.Loading)
        try {
            val query = fireStore.collection(Constants.USER_COLLECTION)
                .whereNotEqualTo(Constants.FIELD_EMAIL, auth.currentUser?.email)
                .whereArrayContains(Constants.FIELD_SEARCH_LIST, name)
                .orderBy(Constants.FIELD_EMAIL, Query.Direction.ASCENDING)
            emit(DataState.Success(query))

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)


}