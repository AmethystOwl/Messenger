package com.example.messenger.home

import androidx.lifecycle.ViewModel
import com.example.messenger.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    // TODO: Implement the ViewModel
}