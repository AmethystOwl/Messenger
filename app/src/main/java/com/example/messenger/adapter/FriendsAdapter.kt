package com.example.messenger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.FriendModelBinding
import com.example.messenger.model.UserProfile

class FriendsAdapter(
    private val onClickListener: OnUserClickListener
) : ListAdapter<UserProfile, FriendsAdapter.UserModelViewHolder>(FriendsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        return UserModelViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    class UserModelViewHolder(private val binding: FriendModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserProfile, onCLickListener: OnUserClickListener) {
            binding.onClickListener = onCLickListener
            binding.data = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): UserModelViewHolder {
                return UserModelViewHolder(
                    FriendModelBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class OnUserClickListener(private val onClickListener: (id: String) -> Unit) {
        fun onClick(id: String) = onClickListener(id)
    }

    class FriendsDiffCallback : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
            oldItem.email == newItem.email


        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
            oldItem == newItem


    }


}

