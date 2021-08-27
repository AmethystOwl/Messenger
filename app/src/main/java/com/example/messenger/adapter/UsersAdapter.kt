package com.example.messenger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.UserModelBinding
import com.example.messenger.model.UserProfile
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class UsersAdapter(
    options: FirestoreRecyclerOptions<UserProfile>,
    private val onClickListener: OnUserClickListener
) :
    FirestoreRecyclerAdapter<UserProfile, UsersAdapter.UserModelViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        return UserModelViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserProfile) {
        holder.bind(model, onClickListener)

    }


    class UserModelViewHolder(private val binding: UserModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserProfile, onCLickListener: OnUserClickListener) {
            binding.onClickListener = onCLickListener
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

    class OnUserClickListener(private val onClickListener: (id: String) -> Unit) {
        fun onClick(id: String) = onClickListener(id)
    }


}

