package com.example.messenger.completeProfile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.R
import com.example.messenger.UserProfile
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.CompleteProfileFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CompleteProfileFragment : Fragment() {
    private val TAG = "CompleteProfileFragment"

    private val viewModel: CompleteProfileViewModel by viewModels()
    lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var binding: CompleteProfileFragmentBinding
    private lateinit var userDoc: DocumentReference
    private var userProfile: UserProfile? = null
    private var uid: String? = null
    private var profilePictureUrl: String? = null

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserProfile()

        getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    profilePictureUrl = uri.toString()
                    uid = viewModel.getAuthUser()?.uid
                    if (uid != null) {
                        Glide.with(this)
                            .load(profilePictureUrl)
                            .into(binding.imageView)
                        binding.submitPictureBtn.isEnabled = true
                    }

                }
            }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CompleteProfileFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.currentProfile.observe(viewLifecycleOwner) {
            if (it?.profilePictureUrl != null) {
                Glide.with(this)
                    .load(userProfile?.profilePictureUrl)
                    .into(binding.imageView)
            }
        }

        binding.browsePictureButton.setOnClickListener {
            openExternalStorage()
        }
        binding.completeLaterButton.setOnClickListener {
            findNavController().navigate(CompleteProfileFragmentDirections.actionCompleteProfileFragmentToChatsFragment())
        }
        binding.submitPictureBtn.setOnClickListener {
            if (profilePictureUrl != null && uid != null) {
                viewModel.uploadImg(Uri.parse(profilePictureUrl), uid!!)
            }

        }

        viewModel.uploadImgState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.submitPictureBtn.isEnabled = false


                }
                is DataState.Success -> {
                    Log.i(TAG, "onCreateView: Profile picture upload completed")
                    binding.imageView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.submitPictureBtn.isEnabled = true
                    getUserProfile()
                    view?.showSnackbar(
                        binding.coordinator,
                        getString(R.string.upload_img_success),
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                    findNavController()
                        .navigate(
                            CompleteProfileFragmentDirections
                                .actionCompleteProfileFragmentToChatsFragment()
                        )
                }

                is DataState.Error -> {
                    Log.i(TAG, "onCreateView: Profile picture err ${it.exception.message!!}")
                    binding.imageView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.submitPictureBtn.isEnabled = false

                    view?.showSnackbar(
                        binding.coordinator,
                        getString(R.string.upload_img_err),
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
                is DataState.Canceled -> {
                    binding.imageView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.submitPictureBtn.isEnabled = false

                    this.view?.showSnackbar(
                        binding.coordinator,
                        getString(R.string.upload_img_cancel),
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
            }
        }


        return binding.root

    }

    @AfterPermissionGranted(Constants.EXTERNAL_REQ_CODE)
    fun openExternalStorage() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openFileChooser()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.storage_permission_required),
                Constants.EXTERNAL_REQ_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun getUserProfile() {
        uid = viewModel.getAuthUser()?.uid
        if (uid != null) {
            userDoc = viewModel.getDocRef(Constants.USER_COLLECTION, uid.toString())
            userDoc.get().addOnCompleteListener {
                userProfile = it.result?.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    viewModel.setCurrentUser(userProfile!!)

                }
            }
        }
    }

    private fun openFileChooser() {
        val pickPictureIntent = Intent()
        pickPictureIntent.type = "image/*"
        pickPictureIntent.action = Intent.ACTION_GET_CONTENT
        getContent.launch("image/*")
    }


    @AfterPermissionGranted(Constants.CAMERA_REQ_CODE)
    fun openCamera() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            TODO("Implement")
        }


    }


}