package com.example.messenger.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.*
import com.example.messenger.model.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.InvalidClassException
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    options: FirestoreRecyclerOptions<Message>,
    private val onMessageClickListener: OnMessageClickListener,
    private val onImageClickListener: OnMessageClickListener,
    private val onRecordingClickListener: OnMessageClickListener,
    private val myId: String
) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    private val TAG = "MessageAdapter"
    private val ITEM_SENDER_TEXT = 0
    private val ITEM_SENDER_IMAGE = 1
    private val ITEM_SENDER_BOTH = 2
    private val ITEM_SENDER_RECORDING = 3

    private val ITEM_RECEIVER_TEXT = 4
    private val ITEM_RECEIVER_IMAGE = 5
    private val ITEM_RECEIVER_BOTH = 6
    private val ITEM_RECEIVER_RECORDING = 7
    private var oldPos = -1


    private class SendImageViewHolder(
        private val binding: SendToImageModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onImageClickListener: OnMessageClickListener,
            position: Int
        ) {
            binding.data = data
            binding.view = binding.imageCardView
            binding.onImageLongClickListener = onImageClickListener
            binding.pos = position
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): SendImageViewHolder {
                return SendImageViewHolder(
                    SendToImageModelBinding.inflate(
                        LayoutInflater.from(parent.context)
                    )
                )
            }
        }


    }

    private class SendTextViewHolder(
        private val binding: SendToModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
            position: Int
        ) {
            binding.data = data
            binding.onMessageLongClickListener = onMessageClickListener
            binding.view = binding.messageTextView
            binding.pos = position
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): SendTextViewHolder {
                return SendTextViewHolder(
                    SendToModelBinding.inflate(
                        LayoutInflater.from(parent.context)
                    )
                )
            }
        }


    }

    private class SendToBothViewModel(
        private val binding: SendToBothBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
            onImageClickListener: OnMessageClickListener,
            position: Int

        ) {
            binding.data = data
            binding.view = binding.messageCardView
            binding.onMessageClickListener = onMessageClickListener
            binding.onImageClickListener = onImageClickListener
            binding.pos = position
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): SendToBothViewModel {
                return SendToBothViewModel(
                    SendToBothBinding.inflate(
                        LayoutInflater.from(parent.context)
                    )
                )
            }
        }
    }


    private class SendRecordingViewModel(
        private val binding: SendRecordingModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onRecordingClickListener: OnMessageClickListener,
            position: Int,
        ) {
            binding.data = data
            binding.view = binding.messageCardView
            binding.play = binding.playImageView
            binding.progressBar = binding.linearProgressIndicator
            binding.onRecordingClickListener = onRecordingClickListener
            binding.pos = position
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): SendRecordingViewModel {
                return SendRecordingViewModel(
                    SendRecordingModelBinding.inflate(
                        LayoutInflater.from(parent.context)
                    )
                )
            }
        }
    }


    private class ReceiveTextViewHolder(
        private val binding: ReceiveFromModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
            position: Int
        ) {
            binding.data = data
            binding.view = binding.messageTextView
            binding.onMessageClickListener = onMessageClickListener
            binding.pos = position
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReceiveTextViewHolder {
                return ReceiveTextViewHolder(
                    ReceiveFromModelBinding
                        .inflate(LayoutInflater.from(parent.context))
                )
            }
        }

    }

    private class ReceiveImageViewHolder(
        private val binding: ReceiveFromImageModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            onImageClickListener: OnMessageClickListener,
            position: Int
        ) {
            binding.data = data
            binding.onImageClickListener = onImageClickListener
            binding.view = binding.imageCardView
            binding.pos = position
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReceiveImageViewHolder {
                return ReceiveImageViewHolder(
                    ReceiveFromImageModelBinding
                        .inflate(LayoutInflater.from(parent.context))
                )
            }
        }

    }

    private class ReceiveBothViewHolder(
        private val binding: ReceiveFromBothBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            OnMessageClickListener: OnMessageClickListener,
            onImageClickListener: OnMessageClickListener,
            position: Int

        ) {
            binding.data = data
            binding.onMessageClickListener = OnMessageClickListener
            binding.onImageClickListener = onImageClickListener
            binding.view = binding.imageCardView
            binding.pos = position
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReceiveBothViewHolder {
                return ReceiveBothViewHolder(
                    ReceiveFromBothBinding
                        .inflate(LayoutInflater.from(parent.context))
                )
            }
        }

    }


    private class ReceiveRecordingViewModel(
        private val binding: ReceiveRecordingModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
            position: Int,
        ) {
            binding.data = data
            binding.view = binding.messageCardView
            binding.onRecordingClickListener = onMessageClickListener
            binding.pos = position
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ReceiveRecordingViewModel {
                return ReceiveRecordingViewModel(
                    ReceiveRecordingModelBinding.inflate(
                        LayoutInflater.from(parent.context)
                    )
                )
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        item.isSender = item.senderUid == myId

        when (item.isSender) {
            true -> {
                return if (item.message != null && item.imageMessageUrl == null) {
                    ITEM_SENDER_TEXT
                } else if (item.message == null && item.imageMessageUrl != null) {
                    ITEM_SENDER_IMAGE
                } else if (item.message != null && item.imageMessageUrl != null) {
                    ITEM_SENDER_BOTH
                } else if (item.message == null && item.imageMessageUrl == null && item.voiceMessageUrl != null) {
                    ITEM_SENDER_RECORDING
                } else {
                    throw InvalidClassException("Invalid Sender View Holder Class")
                }


            }
            false -> {
                return if (item.message != null && item.imageMessageUrl == null) {
                    ITEM_RECEIVER_TEXT
                } else if (item.message == null && item.imageMessageUrl != null) {
                    ITEM_RECEIVER_IMAGE
                } else if (item.message != null && item.imageMessageUrl != null) {
                    ITEM_RECEIVER_BOTH
                } else if (item.message == null && item.imageMessageUrl == null && item.voiceMessageUrl != null) {
                    ITEM_RECEIVER_RECORDING
                } else {
                    throw InvalidClassException("Invalid Receiver View Holder Class")
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            ITEM_SENDER_TEXT -> SendTextViewHolder.from(parent)
            ITEM_SENDER_IMAGE -> SendImageViewHolder.from(parent)
            ITEM_SENDER_BOTH -> SendToBothViewModel.from(parent)
            ITEM_SENDER_RECORDING -> SendRecordingViewModel.from(parent)

            ITEM_RECEIVER_TEXT -> ReceiveTextViewHolder.from(parent)
            ITEM_RECEIVER_IMAGE -> ReceiveImageViewHolder.from(parent)
            ITEM_RECEIVER_BOTH -> ReceiveBothViewHolder.from(parent)
            ITEM_RECEIVER_RECORDING -> ReceiveRecordingViewModel.from(parent)

            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        when (holder) {
            is SendTextViewHolder -> holder.bind(model, onMessageClickListener, position)
            is SendImageViewHolder -> holder.bind(model, onImageClickListener, position)
            is SendRecordingViewModel -> holder.bind(model, onRecordingClickListener, position)
            is SendToBothViewModel -> holder.bind(
                model,
                onMessageClickListener,
                onImageClickListener,
                position
            )

            is ReceiveTextViewHolder -> holder.bind(model, onMessageClickListener, position)
            is ReceiveImageViewHolder -> holder.bind(model, onImageClickListener, position)
            is ReceiveRecordingViewModel -> holder.bind(model, onRecordingClickListener, position)

            is ReceiveBothViewHolder -> holder.bind(
                model,
                onMessageClickListener,
                onImageClickListener,
                position
            )

        }

    }

    class OnMessageClickListener(
        private val onClickListener: ((message: Message, v: View, position: Int) -> Unit)?,
        private val onLongClickListener: ((message: Message, v: View, position: Int) -> Boolean)?,
        private val onRecordingClickListener: ((message: Message, v: View, progress: LinearProgressIndicator, position: Int) -> Unit)?,

        ) {
        fun onClick(message: Message, v: View, position: Int) =
            onClickListener?.let { it(message, v, position) }

        fun onLongClick(message: Message, v: View, position: Int) =
            onLongClickListener?.let { it(message, v, position) }

        fun onPlay(message: Message, v: View, progress: LinearProgressIndicator, position: Int) =
            onRecordingClickListener?.let { it(message, v, progress, position) }
    }

    fun setItemSentStatus(pos: Int, status: Boolean) {
        try {
            getItem(pos).isSent = status
            notifyItemChanged(pos)
        } catch (e: Exception) {
            Log.i(TAG, "setItemSentStatus: ${e.message!!}")
        }

    }

    fun setItemChecked(pos: Int) {
        try {
            if (pos == oldPos) {
                getItem(pos).isChecked = !getItem(pos).isChecked
            } else {
                if (oldPos != -1) {
                    getItem(oldPos).isChecked = false
                }
                getItem(pos).isChecked = true
            }
            notifyItemChanged(pos)
            notifyItemChanged(oldPos)
            oldPos = pos

        } catch (e: Exception) {
            Log.i(TAG, "setItemChecked: ${e.message!!}")
        }

    }


    fun setRecordingProgress(pos: Int, progress: Int) {
        val buf = StringBuffer()
        buf.append(
            String.format(
                "%02d",
                (progress % (1000 * 60 * 60) / (1000 * 60))
            )
        )
            .append(":")
            .append(
                String.format(
                    "%02d",
                    (progress % (1000 * 60 * 60) % (1000 * 60) / 1000)
                )
            )

        try {

        } catch (e: Exception) {
            Log.i(TAG, "setRecordingProgress: ${e.message!!}")
        }

    }

    fun setIsPlaying(pos: Int) {
        if (pos == oldPos) {
            return
        }
        getItem(pos).isPlayingRecord = true
        notifyItemChanged(pos)
        oldPos = pos
    }

    fun onDonePlayingRecording() {

    }

}