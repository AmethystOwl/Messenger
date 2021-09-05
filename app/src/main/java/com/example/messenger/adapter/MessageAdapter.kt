package com.example.messenger.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.ReceiveFromModelBinding
import com.example.messenger.databinding.SendToModelBinding
import com.example.messenger.model.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    options: FirestoreRecyclerOptions<Message>,
    private val onMessageClickListener: OnMessageClickListener,
    private val myId: String
) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    private val TAG = "MessageAdapter"
    private val ITEM_SENDER_MESSAGE = 0
    private val ITEM_RECEIVER_MESSAGE = 1

    class SendViewHolder(
        private val binding: SendToModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Message, onMessageClickListener: OnMessageClickListener) {
            binding.data = data
            binding.onMessageClickListener = onMessageClickListener
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


    }

    class ReceiveViewHolder(
        private val binding: ReceiveFromModelBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Message, onMessageClickListener: OnMessageClickListener) {
            binding.data = data
            binding.onMessageClickListener = onMessageClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReceiveViewHolder {
                return ReceiveViewHolder(
                    ReceiveFromModelBinding
                        .inflate(LayoutInflater.from(parent.context))
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).senderUid == myId) {
            true -> ITEM_SENDER_MESSAGE
            false -> ITEM_RECEIVER_MESSAGE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_SENDER_MESSAGE -> SendViewHolder.from(parent)
            ITEM_RECEIVER_MESSAGE -> ReceiveViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        when (holder) {
            is SendViewHolder -> {
                holder.bind(model, onMessageClickListener)
            }
            is ReceiveViewHolder -> {
                holder.bind(model, onMessageClickListener)
            }
        }
    }

    class OnMessageClickListener(private val onClickListener: ((message: Message) -> Unit)) {
        fun onClick(message: Message) = onClickListener(message)
    }

    fun setItemSentStatus(pos: Int, status: Boolean) {
        try {
            getItem(pos).isSent = status
            notifyItemChanged(pos)
        } catch (e: Exception) {
            Log.i(TAG, "setItemSentStatus: e")
        }

    }
}