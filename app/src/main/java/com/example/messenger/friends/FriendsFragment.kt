package com.example.messenger.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messenger.R

class FriendsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsFragment()
    }

    private lateinit var viewModel: FriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.friends_fragment, container, false)
    }


}