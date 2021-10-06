package com.example.messenger.di


import com.example.messenger.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideRepository(
        auth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        storage: FirebaseStorage
    ) =
        Repository(auth, fireStore, storage)

    @Singleton
    @Provides
    fun provideAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideStorage() = FirebaseStorage.getInstance()


    /* @Singleton
     @Provides
     fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
         .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
         .setUsage(AudioAttributes.USAGE_MEDIA)
         .build()

     @Singleton
     @Provides
     // add annotation to specify IN or OUT
     fun provideAudioRecordFormat(): AudioFormat = AudioFormat.Builder()
         .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
         .setSampleRate(Constants.SAMPLING_RATE_IN_HZ)
         .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
         .build()

     @SuppressLint("MissingPermission")
     @Singleton
     @Provides
     fun provideAudioRecorder() =
         AudioRecord(
             MediaRecorder.AudioSource.DEFAULT,
             Constants.SAMPLING_RATE_IN_HZ,
             Constants.CHANNEL_CONFIG,
             Constants.AUDIO_FORMAT,
             Constants.BUFFER_SIZE
         )*/

}

