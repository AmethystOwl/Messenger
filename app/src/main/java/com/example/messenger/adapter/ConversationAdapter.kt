package com.example.messenger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.ConversationModelBinding
import com.example.messenger.model.Conversation
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ConversationAdapter(options: FirestoreRecyclerOptions<Conversation>) :
    FirestoreRecyclerAdapter<Conversation, ConversationAdapter.ChatModelViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        return ChatModelViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: Conversation) {
        holder.bind(model)
    }

    class ChatModelViewHolder(private val binding: ConversationModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Conversation) {
            binding.data = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ChatModelViewHolder {
                return ChatModelViewHolder(
                    ConversationModelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}

