package com.example.messenger.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.R
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class LoginFragment @Inject constructor() : Fragment() {
    private val TAG = "LoginFragment"

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private val viewModel: LoginViewModel by viewModels()

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.refreshCurrentUser()
        if (viewModel.currentUser.value != null) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToChatsFragment())
        }

        binding.registerButton.setOnClickListener {
            findNavController()
                .navigate(
                    LoginFragmentDirections
                        .actionLoginFragmentToRegisterFragment()
                )
        }
        binding.loginButton.setOnClickListener {
            viewModel.setError(null)
            val email = binding.emailTextInputEt.text.toString()
            val password = binding.passwordTextInputEt.text.toString()
            when (checkInput(email, password)) {
                0 -> {
                    viewModel.login(email, password)
                    binding.loginButton.isEnabled = false

                }
                else -> {
                }
            }


        }
        viewModel.currentUserProfile.observe(viewLifecycleOwner) { userProfileState ->
            when (userProfileState) {
                is DataState.Success -> {
                    if (userProfileState.data?.profilePictureUrl == null) {
                        findNavController()
                            .navigate(
                                LoginFragmentDirections
                                    .actionLoginFragmentToCompleteProfileFragment()
                            )
                    } else {
                        findNavController()
                            .navigate(
                                LoginFragmentDirections
                                    .actionLoginFragmentToChatsFragment()
                            )
                    }
                }
            }
        }
        viewModel.loginState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.loginButton.isEnabled = false
                    binding.progressCircular.visibility = View.VISIBLE

                }
                is DataState.Success -> {
                    binding.loginButton.isEnabled = true
                    binding.progressCircular.visibility = View.INVISIBLE
                    viewModel.refreshCurrentUser()

                }
                is DataState.Invalid -> {
                    binding.loginButton.isEnabled = true

                    binding.progressCircular.visibility = View.INVISIBLE
                    if (it.data == Constants.LOGIN_INVALID_CREDENTIALS) {
                        viewModel.setError(getString(R.string.account_invalid_password))
                    } else if (it.data == Constants.LOGIN_NO_USER) {
                        viewModel.setError(getString(R.string.account_not_found))
                    }
                }
                is DataState.Error -> {
                    binding.loginButton.isEnabled = true
                    binding.progressCircular.visibility = View.INVISIBLE
                    viewModel.setError(it.exception.message!!)
                }
                is DataState.Canceled -> {
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.loginButton.isEnabled = true
                    requireView().showSnackbar(
                        binding.coordinator,
                        "Operation canceled",
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
            }
        }
        viewModel.errorString.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.errorTv.visibility = View.INVISIBLE
            } else {
                binding.errorTv.visibility = View.VISIBLE

            }
        }
        return binding.root
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun checkInput(email: String, password: String): Int {
        if (email.isBlank()) {
            binding.emailTextInputLayout.isErrorEnabled = true
            binding.emailTextInputLayout.error = "Enter your Email Address"
            binding.emailTextInputLayout.requestFocus()
            return -1
        }
        binding.emailTextInputLayout.error = null
        if (viewModel.isValidEmail(email) == Constants.INVALID_EMAIL_FORMAT) {
            binding.emailTextInputLayout.isErrorEnabled = true
            binding.emailTextInputLayout.error = "Enter a valid Email Address"
            binding.emailTextInputLayout.requestFocus()
            return -1
        }
        binding.emailTextInputLayout.error = null

        if (password.isBlank()) {
            binding.passwordTextInputLayout.isErrorEnabled = true
            binding.passwordTextInputLayout.error = "Enter your Password"
            binding.passwordTextInputLayout.requestFocus()
            return -1
        }
        binding.passwordTextInputLayout.error = null

        if (viewModel.isValidPassword(password) == Constants.SHORT_PASSWORD) {
            binding.passwordTextInputLayout.isErrorEnabled = true
            binding.passwordTextInputLayout.error = "Password too short, at least 8 digits"
            binding.passwordTextInputLayout.requestFocus()
            return -1
        }
        binding.passwordTextInputLayout.error = null

        return 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}