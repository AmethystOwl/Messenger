package com.example.messenger.di

import com.example.messenger.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRepository(auth: FirebaseAuth, fireStore: FirebaseFirestore) =
        Repository(auth, fireStore)

    @Singleton
    @Provides
    fun provideAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()


}