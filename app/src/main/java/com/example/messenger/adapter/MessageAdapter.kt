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
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    options: FirestoreRecyclerOptions<Message>,
    private val onMessageClickListener: OnMessageClickListener,
    private val onImageClickListener: OnMessageClickListener,
    private val myId: String
) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    private val TAG = "MessageAdapter"
    private val ITEM_SENDER_TEXT = 0
    private val ITEM_SENDER_IMAGE = 1
    private val ITEM_SENDER_BOTH = 2
    private val ITEM_RECEIVER_TEXT = 3
    private val ITEM_RECEIVER_IMAGE = 4
    private val ITEM_RECEIVER_BOTH = 5

    class SendImageViewHolder(
        private val binding: SendToImageModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
        ) {
            binding.data = data
            binding.view = binding.imageCardView
            binding.onMessageClickListener = onMessageClickListener
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

    class SendTextViewHolder(
        private val binding: SendToModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
        ) {
            binding.data = data
            binding.view = binding.messageTextView
            binding.onMessageClickListener = onMessageClickListener
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

    class SendToBothViewModel(
        private val binding: SendToBothBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Message,
            onImageClickListener: OnMessageClickListener,
            onMessageClickListener: OnMessageClickListener

        ) {
            binding.data = data
            binding.view = binding.messageCardView
            binding.onImageClickListener = onImageClickListener
            binding.onMessageClickListener = onMessageClickListener
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

    /* class SendViewHolder(
         private val binding: SendToModelBinding,
     ) :
         RecyclerView.ViewHolder(binding.root) {

         fun bind(
             data: Message,
             onMessageClickListener: OnMessageClickListener,
             onImageClickListener: OnMessageClickListener,
         ) {
             binding.data = data
             binding.view = binding.messageTextView
             binding.onMessageClickListener = onMessageClickListener
             binding.onImageClickListener = onImageClickListener
             binding.executePendingBindings()

         }

         companion object {
             fun from(parent: ViewGroup): SendViewHolder {
                 return SendViewHolder(
                     SendToModelBinding.inflate(
                         LayoutInflater.from(parent.context)
                     )
                 )
             }
         }


     }*/

    class ReceiveTextViewHolder(
        private val binding: ReceiveFromModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,

            ) {
            binding.data = data
            binding.view = binding.messageTextView
            binding.onMessageClickListener = onMessageClickListener

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

    class ReceiveImageViewHolder(
        private val binding: ReceiveFromImageModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            onImageClickListener: OnMessageClickListener,
        ) {
            binding.data = data
            binding.onImageClickListener = onImageClickListener
            binding.view = binding.imageCardView

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

    class ReceiveBothViewHolder(
        private val binding: ReceiveFromBothBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Message,
            onMessageClickListener: OnMessageClickListener,
            onImageClickListener: OnMessageClickListener

        ) {
            binding.data = data
            binding.onMessageClickListener = onMessageClickListener
            binding.onImageClickListener = onImageClickListener
            binding.view = binding.imageCardView
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

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        when (item.senderUid == myId) {
            // sender
            true -> {
                return if (item.message != null && item.imageMessageUrl == null) {
                    ITEM_SENDER_TEXT
                } else if (item.message == null && item.imageMessageUrl != null) {
                    ITEM_SENDER_IMAGE
                } else {
                    ITEM_SENDER_BOTH
                }

            }
            false -> {
                return if (item.message != null && item.imageMessageUrl == null) {
                    ITEM_RECEIVER_TEXT
                } else if (item.message == null && item.imageMessageUrl != null) {
                    ITEM_RECEIVER_IMAGE
                } else {
                    ITEM_RECEIVER_BOTH
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            ITEM_SENDER_TEXT -> SendTextViewHolder.from(parent)
            ITEM_SENDER_IMAGE -> SendImageViewHolder.from(parent)
            ITEM_SENDER_BOTH -> SendToBothViewModel.from(parent)
            ITEM_RECEIVER_TEXT -> ReceiveTextViewHolder.from(parent)
            ITEM_RECEIVER_IMAGE -> ReceiveImageViewHolder.from(parent)
            ITEM_RECEIVER_BOTH -> ReceiveBothViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        when (holder) {
            is SendTextViewHolder -> holder.bind(model, onMessageClickListener)
            is SendImageViewHolder -> holder.bind(model, onImageClickListener)
            is SendToBothViewModel -> holder.bind(
                model,
                onImageClickListener,
                onMessageClickListener
            )

            is ReceiveTextViewHolder -> holder.bind(model, onMessageClickListener)
            is ReceiveImageViewHolder -> holder.bind(model, onImageClickListener)
            is ReceiveBothViewHolder -> holder.bind(
                model,
                onMessageClickListener,
                onImageClickListener
            )

        }

    }

    class OnMessageClickListener(private val onClickListener: (message: Message, v: View) -> Unit) {
        fun onClick(message: Message, v: View) = onClickListener(message, v)
    }

    fun setItemSentStatus(pos: Int, status: Boolean) {
        try {
            getItem(pos).isSent = status
            notifyItemChanged(pos)
        } catch (e: Exception) {
            Log.i(TAG, "setItemSentStatus: ${e.message!!}")
        }

    }


}