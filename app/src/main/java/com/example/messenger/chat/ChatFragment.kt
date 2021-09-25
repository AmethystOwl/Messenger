package com.example.messenger.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordPermissionHandler
import com.example.messenger.*
import com.example.messenger.adapter.MessageAdapter
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.model.Message
import com.example.messenger.model.UserProfile
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.piruin.quickaction.ActionItem
import me.piruin.quickaction.QuickAction
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {
    private val TAG = "ChatFragment"
    private val ACTION_COPY_TEXT = 1
    private val ACTION_DELETE_TEXT = 2


    private lateinit var messageAdapter: MessageAdapter
    private var friendProfile: UserProfile? = null
    private var currentUserProfile: UserProfile? = null
    private var friendUid: String? = null

    private val chatViewModel: ChatViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private var mainActivity: MainActivity? = null
    private var _binding: ChatFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var myId: String
    private var selectedMsg: Message? = null
    private var lastPos: Int = -1

    // TODO : lma y3ml pick l image,
    //  redirect 3la fragment tnya y2dr y7ot text t7t el image,
    //  w lma ydos 3la el send button yrg3o ll fragment de,
    //  shared viewModel zy el ViewImageFragment
    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data?.data != null) {
                    //  val message = Message(
                    //     message = if (binding.sendTextEditText.text.trim()
                    //            .isEmpty()
                    //    ) null else binding.sendTextEditText.text.toString(),
                    //   senderUid = myId,
                    //   profilePictureUrl = currentUserProfile?.profilePictureUrl,
                    //)
                    val imageUri = it.data?.data!!
                    //    chatViewModel.sendImageMessage(message, imageUri, friendUid!!)
                    //   binding.sendTextEditText.text.clear()

                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = chatViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recordButton.setRecordView(binding.recordView)


        binding.recordView.setRecordPermissionHandler(RecordPermissionHandler {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return@RecordPermissionHandler true
            }
            if (hasAudioRecordPermission()) {
                true
            } else {
                requestAudioRecordPermission()
                false
            }


        })
        binding.recordButton.isListenForRecord = true
        binding.recordView.setOnRecordListener(object : OnRecordListener {

            override fun onStart() {
                binding.messageLayout.visibility = View.GONE
                binding.recordView.visibility = View.VISIBLE
                val arr: IntArray? = null
                binding.recordButton.getLocationOnScreen(arr)
                Log.i(TAG, "onLessThanSecond: $arr!!")

            }

            override fun onCancel() {
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE
                val arr: IntArray? = null
                binding.recordButton.getLocationOnScreen(arr)
                Log.i(TAG, "onLessThanSecond: $arr!!")

            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE

                val arr: IntArray? = null
                binding.recordButton.getLocationOnScreen(arr)
                Log.i(TAG, "onLessThanSecond: $arr!!")

            }

            override fun onLessThanSecond() {
                binding.recordView.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE

                val arr: IntArray? = null
                binding.recordButton.getLocationOnScreen(arr)
                Log.i(TAG, "onLessThanSecond: $arr!!")
            }


        })
        myId = sharedViewModel.getCurrentUser()?.uid!!
        friendUid = ChatFragmentArgs.fromBundle(requireArguments()).friendUId
        /*  if (binding.sendTextEditText.text.trim().isEmpty()) {
              binding.fab.background =
                  AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_mic_24)
          } else {
              binding.fab.background =
                  AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_send_24)
          }

          binding.sendTextEditText.addTextChangedListener(object : TextWatcher {
              override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, cound: Int) {

              }

              override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                  if (s?.trim()?.isEmpty()!!) {
                      binding.fab.setImageResource(R.drawable.ic_baseline_mic_24)
                  } else {
                      binding.fab.setImageResource(R.drawable.ic_baseline_send_24)

                  }
              }

              override fun afterTextChanged(s: Editable?) {

              }

          })
  */

        setupAdapter()

        sharedViewModel.getCurrentUserProfile()
        chatViewModel.friendInfo(friendUid!!)

        chatViewModel.selectedMessagePosition.observe(viewLifecycleOwner) { selectedMessagePosition ->
            selectedMessagePosition?.let {
                lastPos = it
            }

        }
        chatViewModel.observeDocChanges(friendUid!!)
        chatViewModel.documentChanges.observe(viewLifecycleOwner) { docChangesDataState ->
            when (docChangesDataState) {
                is DataState.Error -> {

                }
                is DataState.Success -> {
                    // TODO : it works but it's not the desired result, it goes to bottom then scrolls back, also not very accurate
                    if (docChangesDataState.data?.equals(Constants.DOCUMENT_ADDED)!!) {
                        if (lastPos == -1) {
                            binding.chatRecyclerview.scrollToPosition(0)
                            lastPos = -1
                        } else {
                            binding.chatRecyclerview.scrollToPosition(lastPos)

                        }
                    }
                }

                else -> {

                }


            }

        }
        sharedViewModel.currentUserProfileState.observe(viewLifecycleOwner)
        { currentUserProfileDataState ->
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
                        binding.chatRecyclerview.scrollToPosition(0)

                    }
                    is DataState.Progress -> {
                        Log.i(TAG, "onCreateView: ${imageMessageState.data!!}")
                    }
                    is DataState.Success -> {
                        // sent to server, add checkmark
                        // all old messages are using "sending" icon for now...
                        messageAdapter.setItemSentStatus(0, true)


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
        }

        /*     binding.fab.setOnClickListener {
                 if (binding.sendTextEditText.text.trim()
                         .isNotEmpty()
                 ) {
                     // send message
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

             }
             binding.selectImageButton.setOnClickListener {
                 ImagePicker.with(this).createIntent { imageIntent ->
                     getImage.launch(imageIntent)
                 }


             }
         }

         binding.fab.setOnTouchListener { v, event ->
             if (binding.sendTextEditText.text.trim().isEmpty()) {
                 when (event.action) {
                     MotionEvent.ACTION_DOWN -> {
                         if (!hasAudioRecordPermission()) {
                             requestAudioRecordPermission()
                         } else {
                             // TODO : Start recording
                           //  var recorder = AudioRecord()

                         }
                     }
                     MotionEvent.ACTION_UP -> {
                         // TODO : Stop recording and send
                     }
                     MotionEvent.ACTION_CANCEL -> {
                         // TODO : Cancel
                     }
                 }

             }
             false
         }
 */
        return binding.root
    }


    private fun requestAudioRecordPermission() {
        if (!hasAudioRecordPermission()) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.record_audio_permission_required),
                Constants.AUDIO_RECORD_PERMISSION_REQ_CODE,
                Manifest.permission.RECORD_AUDIO
            )
        }

    }

    private fun hasAudioRecordPermission() =
        EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)

    val onMessageClickListener = MessageAdapter.OnMessageClickListener(
        onClickListener = object : (Message, View, Int) -> Unit {
            override fun invoke(message: Message, v: View, pos: Int) {
                messageAdapter.setItemChecked(pos, true)
                if (pos == 0) {
                    binding.chatRecyclerview.scrollToPosition(0)
                }

            }

        },
        onLongClickListener = object : (Message, View, Int) -> Boolean {
            override fun invoke(message: Message, v: View, pos: Int): Boolean {
                selectedMsg = message
                val messageQuickAction = QuickAction(requireContext(), QuickAction.HORIZONTAL)
                messageQuickAction.setColor(Color.DKGRAY)
                messageQuickAction.setTextColor(Color.WHITE)
                messageQuickAction.setEnabledDivider(true)
                val copyAction =
                    ActionItem(ACTION_COPY_TEXT, getString(R.string.copy_text))
                val deleteAction =
                    ActionItem(ACTION_DELETE_TEXT, getString(R.string.delete_message))
                messageQuickAction.addActionItem(copyAction, deleteAction)
                messageQuickAction.show(v)
                messageQuickAction.setOnActionItemClickListener {
                    when (it.actionId) {
                        ACTION_COPY_TEXT -> {
                            val clipboard =
                                requireActivity().getSystemService(Service.CLIPBOARD_SERVICE) as ClipboardManager
                            if (selectedMsg?.message != null) {
                                val clipData =
                                    ClipData.newPlainText("copyMessage", selectedMsg?.message)
                                clipboard.setPrimaryClip(clipData)
                                Toast.makeText(
                                    requireContext(),
                                    "Message copied to Clipboard",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                        ACTION_DELETE_TEXT -> {

                        }
                    }
                }

                return true
            }


        }


    )
    val onImageClickListener = MessageAdapter.OnMessageClickListener(onClickListener = object :
            (Message, View, Int) -> Unit {
        override fun invoke(message: Message, v: View, pos: Int) {
            if (message.imageMessageUrl != null) {
                val test =
                    (binding.chatRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                chatViewModel.setSelectedMessage(message, test)

                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToImageFragment())
            }

        }
    }, onLongClickListener = object : (Message, View, Int) -> Boolean {
        override fun invoke(message: Message, v: View, pos: Int): Boolean {
            messageAdapter.setItemChecked(pos, true)
            if (pos == 0) {
                binding.chatRecyclerview.scrollToPosition(0)
            }
            return true
        }

    }

    )


    // TODO : use blurred image while image loads(stackoverflow)
    private fun setupAdapter() {
        val query = sharedViewModel.getDefaultMessageQuery(friendUid!!)
        val option =
            FirestoreRecyclerOptions
                .Builder<Message>()
                .setQuery(query, Message::class.java)
                .build()

        messageAdapter =
            MessageAdapter(option, onMessageClickListener, onImageClickListener, myId)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        // added to prevent "Inconsistency detected. Invalid view holder adapter position"
        binding.chatRecyclerview.itemAnimator = null
        // if this is removed, it doesn't scroll to the bottom for some reason
        messageAdapter.snapshots.addChangeEventListener(object : ChangeEventListener {
            override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DocumentSnapshot,
                newIndex: Int,
                oldIndex: Int
            ) {

            }

            override fun onDataChanged() {

            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e(TAG, "onError: ${e.message}", e)
            }

        })

        binding.chatRecyclerview.layoutManager = linearLayoutManager
        binding.chatRecyclerview.adapter = messageAdapter
        binding.chatRecyclerview.setHasFixedSize(true)
    }


    override fun onStart() {
        super.onStart()
        chatViewModel.setSelectedMessage(null, null)
        mainActivity = activity as MainActivity
        messageAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        messageAdapter.stopListening()
        mainActivity = null
        currentUserProfile = null
        selectedMsg = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}