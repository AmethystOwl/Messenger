package com.example.messenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.UserModelBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class UsersAdapter(options: FirestoreRecyclerOptions<UserProfile>) :
    FirestoreRecyclerAdapter<UserProfile, UsersAdapter.UserModelViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        return UserModelViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserProfile) {
        holder.bind(model)
    }

    class UserModelViewHolder(private val binding: UserModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserProfile) {
            binding.data = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): UserModelViewHolder {
                return UserModelViewHolder(
                    UserModelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

