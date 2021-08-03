package com.example.messenger.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor() : Fragment() {
    private val TAG = "LoginFragment"

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: LoginFragmentBinding

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        /*val auth = viewModel.getAuth()
        if (auth.currentUser != null) {
            context?.snackBar(binding.coordinator, "User logged in.")
            Log.i(TAG, "onCreateView: ")
            // findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }*/

        binding.registerButton.setOnClickListener {
            findNavController()
                .navigate(
                    LoginFragmentDirections
                        .actionLoginFragmentToRegisterFragment()
                )
        }
        binding.loginButton.setOnClickListener {
            // get current user profile, check for profile completion.. redirect accordingly

        }
        return binding.root
    }
}