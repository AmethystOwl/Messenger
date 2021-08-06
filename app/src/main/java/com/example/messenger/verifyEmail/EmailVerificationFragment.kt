package com.example.messenger.verifyEmail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.R
import com.example.messenger.UserProfile
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.EmailVerificationFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class EmailVerificationFragment : Fragment() {
    private val TAG = "EmailVerificationFragm"

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private val viewModel: EmailVerificationViewModel by viewModels()

    private lateinit var binding: EmailVerificationFragmentBinding
    private var userProfile: UserProfile? = null
    private lateinit var userDoc: DocumentReference

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmailVerificationFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val uid = viewModel.getAuthUser()?.uid
        if (uid != null) {
            userDoc = viewModel.getDocRef(Constants.USER_COLLECTION, uid.toString())
            userDoc.get().addOnCompleteListener {
                userProfile = it.result?.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    viewModel.setCurrentUser(userProfile!!)
                    if (viewModel.isCounterActivated.value == false) {
                        viewModel.countDown()
                    }

                    viewModel.counterValue.observe(viewLifecycleOwner) { data ->
                        if (viewModel.isCounterActivated.value == true) {
                            binding.counter.text = (data / 1000).toInt().toString()
                        }
                    }
                    viewModel.isCounterActivated.observe(viewLifecycleOwner) { state ->
                        if (state == false) {
                            binding.resendCodeBtn.isEnabled = true
                        } else if (state == true) {
                            binding.resendCodeBtn.isEnabled = false

                        }
                    }
                }
            }
        } else {
            requireView().showSnackbar(
                binding.coordinator,
                getString(R.string.id_retrieval_failed),
                Snackbar.LENGTH_LONG,
                null,
                null
            )
            // ????
        }
        binding.verifyLaterTv.setOnClickListener {
            findNavController()
                .navigate(
                    EmailVerificationFragmentDirections.actionEmailVerificationFragmentToCompleteProfileFragment()
                )
        }
        binding.resendCodeBtn.setOnClickListener {
            if (viewModel.isCounterActivated.value == false) {
                viewModel.resendVerificationCode()
                binding.resendCodeBtn.isEnabled = false
                requireView().showSnackbar(
                    binding.coordinator,
                    getString(R.string.email_verification_sent),
                    Snackbar.LENGTH_LONG,
                    null,
                    null
                )
            }
        }
        binding.verificationDoneBtn.setOnClickListener {
            viewModel.getAuthUser()?.reload()?.addOnCompleteListener {
                if (viewModel.isEmailVerified()!!) {
                    findNavController()
                        .navigate(
                            EmailVerificationFragmentDirections.actionEmailVerificationFragmentToCompleteProfileFragment()
                        )
                } else if (!viewModel.isEmailVerified()!!) {
                    requireView().showSnackbar(
                        binding.coordinator,
                        getString(R.string.email_not_verified),
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
            }

        }
        viewModel.emailVerificationState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    if (dataState.data == Constants.VERIFICATION_EMAIL_SENT_SUCCESS) {
                        Log.i(TAG, "onCreateView: email verification success")
                    } else if (dataState.data == Constants.EMAIL_VERIFIED) {
                        Log.i(TAG, "onCreateView: Email already verified")

                    }
                }
                is DataState.Error -> {

                    requireView().showSnackbar(
                        binding.coordinator,
                        getString(R.string.email_verification_failed_err).format(dataState.exception),
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                    Log.i(TAG, "Email Verification failed with error ${dataState.exception}")

                }
                else -> {

                }
            }
        }

        return binding.root

    }
}