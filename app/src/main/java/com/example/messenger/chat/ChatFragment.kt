package com.example.messenger.chat

import android.app.Activity
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.DataState
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.SharedViewModel
import com.example.messenger.adapter.MessageAdapter
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.model.Message
import com.example.messenger.model.UserProfile
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.piruin.quickaction.ActionItem
import me.piruin.quickaction.QuickAction


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {
    private val TAG = "ChatFragment"
    private val ACTION_COPY_TEXT = 1
    private val ACTION_DELETE_TEXT = 2
    private val ACTION_SAVE_IMAGE = 3
    private val ACTION_DELETE_IMAGE = 4

    private lateinit var messageAdapter: MessageAdapter
    private var friendProfile: UserProfile? = null
    private var currentUserProfile: UserProfile? = null
    private var friendUid: String? = null

    private val chatViewModel: ChatViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: ChatFragmentBinding
    private lateinit var myId: String
    private var selectedMsg: Message? = null
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
                    binding.sendTextEditText.text.clear()

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
        if (binding.sendTextEditText.text.isEmpty()) {
            setSendButtonState(false)
        }
        binding.sendTextEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, cound: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 0) {
                    setSendButtonState(true)
                } else {
                    setSendButtonState(false)

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        setupAdapter()

        sharedViewModel.getCurrentUserProfile()
        chatViewModel.friendInfo(friendUid!!)
        sharedViewModel.currentUserProfileState.observe(viewLifecycleOwner) { currentUserProfileDataState ->
            when (currentUserProfileDataState) {
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    currentUserProfile = currentUserProfileDataState.data
                }
                is DataState.Canceled -> {

                }
                is DataState.Error -> {

                }
                else -> {

                }
            }
            chatViewModel.friendUserDataState.observe(viewLifecycleOwner) { friendProfileDataState ->
                when (friendProfileDataState) {
                    is DataState.Loading -> {

                    }
                    is DataState.Success -> {
                        friendProfile = friendProfileDataState.data

                        chatViewModel.setFriendProfile(friendProfile!!)

                    }
                    is DataState.Canceled -> {

                    }
                    is DataState.Error -> {

                    }
                    else -> {

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
                        // binding.chatRecyclerview.smoothScrollToPosition(0)

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
                    else -> {

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
                        //  binding.chatRecyclerview.scrollToPosition(0)


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
                    else -> {

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
                    binding.sendTextEditText.text.clear()
                }
            }
            binding.selectImageButton.setOnClickListener {
                ImagePicker.with(this).createIntent { imageIntent ->
                    getImage.launch(imageIntent)
                }


            }
        }

        return binding.root
    }

    val onMessageClickListener =
        MessageAdapter.OnMessageClickListener { message, view ->
            selectedMsg = message
            val messageQuickAction = QuickAction(requireContext(), QuickAction.HORIZONTAL)
            messageQuickAction.setColor(android.graphics.Color.DKGRAY)
            messageQuickAction.setTextColor(android.graphics.Color.WHITE)
            messageQuickAction.setEnabledDivider(true)
            val copyAction =
                ActionItem(ACTION_COPY_TEXT, getString(R.string.copy_text))
            val deleteAction =
                ActionItem(ACTION_DELETE_TEXT, getString(R.string.delete_message))
            messageQuickAction.addActionItem(copyAction, deleteAction)
            messageQuickAction.show(view)
            messageQuickAction.setOnActionItemClickListener {
                when (it.actionId) {
                    ACTION_COPY_TEXT -> {
                        val clipboard =
                            requireActivity().getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
                        if (selectedMsg?.message != null) {
                            val clipData = ClipData.newPlainText("uuh", selectedMsg?.message)
                            clipboard.setPrimaryClip(clipData)
                            Toast.makeText(
                                requireContext(),
                                "Message copied",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    ACTION_DELETE_TEXT -> {

                    }
                }
            }
            // TODO : copy, delete...

        }
    val onImageClickListener = MessageAdapter.OnMessageClickListener { message, view ->

        val imageQuickAction = QuickAction(requireContext(), QuickAction.HORIZONTAL)
        imageQuickAction.setColor(android.graphics.Color.DKGRAY)
        imageQuickAction.setTextColor(android.graphics.Color.WHITE)
        imageQuickAction.setAnimStyle(QuickAction.Animation.GROW_FROM_CENTER)
        imageQuickAction.setEnabledDivider(true)
        val copyAction =
            ActionItem(
                ACTION_SAVE_IMAGE,
                getString(R.string.save_image)
            )
        val deleteAction =
            ActionItem(
                ACTION_DELETE_IMAGE,
                getString(R.string.delete_message)
            )
        imageQuickAction.addActionItem(copyAction, deleteAction)
        imageQuickAction.show(view)
        imageQuickAction.setOnActionItemClickListener {
            when (it.actionId) {
                ACTION_SAVE_IMAGE -> {

                }
                ACTION_DELETE_IMAGE -> {

                }
            }
        }
        // TODO : save, delete...
    }

    // TODO : use blurred image while image downloads(stackoverflow)
    private fun setupAdapter() {
        val query = sharedViewModel.getDefaultMessageQuery(friendUid!!)
        val option =
            FirestoreRecyclerOptions
                .Builder<Message>()
                .setQuery(query, Message::class.java)
                .build()

        messageAdapter = MessageAdapter(option, onMessageClickListener, onImageClickListener, myId)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        // added to prevent "Inconsistency detected. Invalid view holder adapter position"
        binding.chatRecyclerview.itemAnimator = null
        messageAdapter.snapshots.addChangeEventListener(object : ChangeEventListener {
            override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DocumentSnapshot,
                newIndex: Int,
                oldIndex: Int
            ) {

            }

            override fun onDataChanged() {
                binding.chatRecyclerview.scrollToPosition(0)

            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e(TAG, "onError: ${e.message}", e)
            }

        })

        binding.chatRecyclerview.layoutManager = linearLayoutManager
        binding.chatRecyclerview.adapter = messageAdapter
        binding.chatRecyclerview.setHasFixedSize(true)
    }

    private fun setSendButtonState(isEnabled: Boolean) {
        when {
            isEnabled -> {
                binding.sendImageButton.setImageResource(R.drawable.ic_baseline_send_blue_48)
            }
            else -> {
                binding.sendImageButton.setImageResource(R.drawable.ic_baseline_send_48)

            }
        }
        binding.sendImageButton.isEnabled = isEnabled

        QuickAction.OnActionItemClickListener {
            when (it.actionId) {
                121 -> {
                    Toast.makeText(requireContext(), it.title, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
        messageAdapter.startListening()
        binding.chatRecyclerview.scrollToPosition(0)

    }

    override fun onStop() {
        super.onStop()
        messageAdapter.stopListening()

    }

}