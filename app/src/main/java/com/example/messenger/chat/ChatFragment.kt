package com.example.messenger.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.messenger.DataState
import com.example.messenger.MainActivity
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.model.UserProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {
    private val TAG = "ChatFragment"
    private lateinit var mainActivity: MainActivity
    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var binding: ChatFragmentBinding
    private var friendProfile: UserProfile? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = chatViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val uId = ChatFragmentArgs.fromBundle(requireArguments()).friendUId
        chatViewModel.friendInfo(uId)
        chatViewModel.friendUserDataState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    friendProfile = it.data
                    chatViewModel.setFriendProfile(friendProfile!!)

                }
                is DataState.Canceled -> {

                }
                is DataState.Error -> {

                }
            }
        }


        return binding.root
    }


}