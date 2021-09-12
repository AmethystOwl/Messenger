package com.example.messenger.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.*
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.RegisterFragmentBinding
import com.example.messenger.model.UserProfile
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val TAG = "RegisterFragment"

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: RegisterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.ccp.registerCarrierNumberEditText(binding.carrierPhoneInputEt)


        binding.ccpCountry.setOnCountryChangeListener {
            binding.ccp.setDefaultCountryUsingNameCode(binding.ccpCountry.defaultCountryNameCode)
        }
        binding.registerButton.setOnClickListener {
            val email = binding.emailTextInputEt.text.toString()
            val password = binding.passwordTextInputEt.text.toString()
            val fname = binding.firstNameTextInputEt.text.toString()
            val lname = binding.lastNameTextInputEt.text.toString()
            val yearOfBirth = binding.yearSpinner.selectedItem.toString().toInt()
            val dateOfBirth =
                binding.daySpinner.selectedItem.toString() + "/" + binding.monthSpinner.selectedItem.toString() + "/" + yearOfBirth
            var fullPhoneNumber = binding.ccp.fullNumberWithPlus
            binding.monthSpinner.selectedItem.toString() + "/" + binding.yearSpinner.selectedItem.toString()
            val gender = when {
                binding.maleRb.isChecked -> 0
                binding.femaleRb.isChecked -> 1
                else -> -1
            }
            if (fname.isEmpty()) {
                binding.firstNameTextInputLayout.isErrorEnabled = true
                binding.firstNameTextInputLayout.error = "Enter your first name"
                binding.firstNameTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.firstNameTextInputLayout.error = null

            if (lname.isEmpty()) {
                binding.lastNameTextInputLayout.isErrorEnabled = true
                binding.lastNameTextInputLayout.error = "Enter your last name"
                binding.lastNameTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.lastNameTextInputLayout.error = null

            if (email.isEmpty()) {
                binding.emailTextInputLayout.isErrorEnabled = true
                binding.emailTextInputLayout.error = "Enter your Email Address"
                binding.emailTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.emailTextInputLayout.error = null

            if (password.isEmpty()) {
                binding.passwordTextInputLayout.isErrorEnabled = true
                binding.passwordTextInputLayout.error = "Enter your Password"
                binding.passwordTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.passwordTextInputLayout.error = null

            if (yearOfBirth >= 2005) {
                it.showSnackbar(
                    binding.coordinator,
                    getString(R.string.too_young),
                    Snackbar.LENGTH_LONG,
                    null,
                    null
                )
                binding.yearSpinner.requestFocus()
                return@setOnClickListener
            }
            if (gender == -1) {
                it.showSnackbar(
                    binding.coordinator,
                    getString(R.string.select_gender),
                    Snackbar.LENGTH_LONG,
                    null,
                    null
                )
                binding.radioGroup.requestFocus()
                return@setOnClickListener
            }
            if (viewModel.isValidEmail(email) == Constants.INVALID_EMAIL_FORMAT) {
                binding.emailTextInputLayout.isErrorEnabled = true
                binding.emailTextInputLayout.error = "Enter a valid Email Address"
                binding.emailTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.emailTextInputLayout.error = null

            if (viewModel.isValidPassword(password) == Constants.SHORT_PASSWORD) {
                binding.passwordTextInputLayout.isErrorEnabled = true
                binding.passwordTextInputLayout.error = "Password too short, at least 8 digits"
                binding.passwordTextInputLayout.requestFocus()
                return@setOnClickListener
            }
            binding.passwordTextInputLayout.error = null

            var isPhoneValid = false
            val isPhoneEmpty: Boolean
            if (!TextUtils.isEmpty(binding.carrierPhoneInputEt.text.toString())) {
                isPhoneEmpty = false
                binding.ccp.setPhoneNumberValidityChangeListener { isValidNumber ->
                    if (!isValidNumber) {
                        binding.carrierPhoneInputLayout.isErrorEnabled = true
                        binding.carrierPhoneInputLayout.error = "Invalid Phone Number"
                        binding.carrierPhoneInputLayout.requestFocus()
                        isPhoneValid = false
                        return@setPhoneNumberValidityChangeListener
                    } else if (isValidNumber) {
                        isPhoneValid = true
                    }
                }
            } else {
                isPhoneEmpty = true
            }

            if (isPhoneValid && !isPhoneEmpty) {
                fullPhoneNumber = binding.ccp.fullNumberWithPlus
            } else if (isPhoneEmpty) {
                fullPhoneNumber = null
            } else if (!isPhoneValid && !isPhoneEmpty) {
                return@setOnClickListener
            }
            binding.carrierPhoneInputLayout.error = null

            val user = UserProfile(
                accountCreationDate = Utils.getTime(),
                fname = fname,
                lname = lname,
                email = email,
                country = binding.ccpCountry.selectedCountryName,
                phoneNumber = fullPhoneNumber,
                dateOfBirth = dateOfBirth,
                status = Constants.STATUS_ONLINE,
                searchList = ArrayList(),
                friendsList = ArrayList()
            )
            viewModel.signUp(user, password)

        }
        viewModel.signUpState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success<UserProfile> -> {
                    Log.i(TAG, "onCreateView: DataState -> Success")
                    binding.registerButton.isEnabled = true
                    binding.progressCircular.visibility = View.INVISIBLE
                    findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToVerifyEmailFragment(
                            dataState.data
                        )
                    )
                    viewModel.doneObserving()
                }

                is DataState.Loading -> {
                    Log.i(TAG, "onCreateView: DataState -> Loading")
                    binding.registerButton.isEnabled = false
                    binding.progressCircular.visibility = View.VISIBLE
                }
                is DataState.Error -> {
                    Log.i(TAG, "onCreateView: DataState -> Error")
                    showMessage(requireView(), dataState.exception.message!!)
                    viewModel.doneObserving()
                    binding.registerButton.isEnabled = true
                    binding.progressCircular.visibility = View.INVISIBLE

                }
                else -> {
                }
            }

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showMessage(view: View, message: String) {
        view.showSnackbar(binding.coordinator, message, Snackbar.LENGTH_LONG, null, null)
    }

}

