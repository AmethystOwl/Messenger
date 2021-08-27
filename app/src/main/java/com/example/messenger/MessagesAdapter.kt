package com.example.messenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.MessageModelBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MessagesAdapter(options: FirestoreRecyclerOptions<Message>) :
    FirestoreRecyclerAdapter<Message, MessagesAdapter.ChatModelViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        return ChatModelViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: Message) {
        holder.bind(model)
    }

    class ChatModelViewHolder(private val binding: MessageModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Message) {
            binding.data = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ChatModelViewHolder {
                return ChatModelViewHolder(
                    MessageModelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}

