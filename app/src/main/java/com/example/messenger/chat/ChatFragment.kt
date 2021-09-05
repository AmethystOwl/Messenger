package com.example.messenger.chat

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.DataState
import com.example.messenger.MainActivity
import com.example.messenger.SharedViewModel
import com.example.messenger.adapter.MessageAdapter
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.model.Message
import com.example.messenger.model.UserProfile
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {
    private val TAG = "ChatFragment"
    private lateinit var messageAdapter: MessageAdapter
    private var friendProfile: UserProfile? = null
    private var currentUserProfile: UserProfile? = null
    private var friendUid: String? = null

    private val chatViewModel: ChatViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: ChatFragmentBinding
    private lateinit var myId: String


    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data?.data != null) {
                    val message = Message(
                        message = binding.sendTextEditText.text.toString(),
                        senderUid = myId,
                        profilePictureUrl = currentUserProfile?.profilePictureUrl,
                    )
                    val imageUri = it.data?.data!!
                    chatViewModel.sendImageMessage(message, imageUri, friendUid!!)

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = chatViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        myId = sharedViewModel.getCurrentUser()?.uid!!
        friendUid = ChatFragmentArgs.fromBundle(requireArguments()).friendUId
        setupAdapter()

        sharedViewModel.getCurrentUserProfile()
        chatViewModel.friendInfo(friendUid!!)
        sharedViewModel.currentUserProfileState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    currentUserProfile = it.data
                }
                is DataState.Canceled -> {

                }
                is DataState.Error -> {

                }
            }
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
            chatViewModel.messageState.observe(viewLifecycleOwner) { messageDataState ->
                when (messageDataState) {
                    is DataState.Loading -> {
                        // sending to server
                        binding.chatRecyclerview.scrollToPosition(0)

                    }
                    is DataState.Success -> {
                        // sent to server, add checkmark
                        // all old messages are using "sending" icon for now...
                        messageAdapter.setItemSentStatus(0, true)
                        binding.chatRecyclerview.scrollToPosition(0)

                    }
                    is DataState.Canceled -> {
                        Log.i(TAG, "onCreateView: Message canceled")

                    }
                    is DataState.Error -> {
                        Log.i(
                            TAG,
                            "onCreateView: Message error : ${messageDataState.exception.message!!}"
                        )

                    }
                }
            }


            chatViewModel.imageMessageState.observe(viewLifecycleOwner) { imageMessageState ->
                when (imageMessageState) {
                    is DataState.Loading -> {
                        // sending to server
                        binding.chatRecyclerview.scrollToPosition(0)

                    }
                    is DataState.Progress -> {
                        Log.i(TAG, "onCreateView: ${imageMessageState.data!!}")
                    }
                    is DataState.Success -> {
                        // sent to server, add checkmark
                        // all old messages are using "sending" icon for now...
                        messageAdapter.setItemSentStatus(0, true)
                        binding.chatRecyclerview.scrollToPosition(0)

                    }
                    is DataState.Canceled -> {
                        Log.i(TAG, "onCreateView: Image Message canceled")

                    }
                    is DataState.Error -> {
                        Log.i(
                            TAG,
                            "onCreateView: Image Message error : ${imageMessageState.exception.message!!}"
                        )

                    }
                }
            }


            binding.sendImageButton.setOnClickListener {
                if (friendUid != null) {
                    val message = Message(
                        message = binding.sendTextEditText.text.toString(),
                        senderUid = myId,
                        profilePictureUrl = currentUserProfile?.profilePictureUrl,

                        )
                    chatViewModel.sendMessage(message, friendUid!!)
                    binding.sendTextEditText.setText("")
                }
            }
            binding.selectImageButton.setOnClickListener {
                ImagePicker.with(this).createIntent {
                    getImage.launch(it)
                }


            }
        }
        return binding.root
    }

    val onMessageClickListener = MessageAdapter.OnMessageClickListener {
        Log.i(TAG, "onCreateView: Message clicked")
        // TODO : copy delete etc?
    }

    private fun setupAdapter() {
        val query = sharedViewModel.getDefaultMessageQuery(friendUid!!)
        val option =
            FirestoreRecyclerOptions
                .Builder<Message>()
                .setQuery(query, Message::class.java)
                .build()

        messageAdapter = MessageAdapter(option, onMessageClickListener, myId)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        // added to stop "Inconsistency detected. Invalid view holder adapter position"
        binding.chatRecyclerview.itemAnimator = null

        binding.chatRecyclerview.layoutManager = linearLayoutManager
        binding.chatRecyclerview.adapter = messageAdapter
        binding.chatRecyclerview.setHasFixedSize(true)
        /*  messageAdapter.registerAdapterDataObserver(
              ChatBottomObserver(binding.chatRecyclerview, messageAdapter, linearLayoutManager)
          )*/
    }

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
        messageAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        messageAdapter.stopListening()

    }

}