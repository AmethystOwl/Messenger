package com.example.messenger.chat.viewImage

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.messenger.Constants
import com.example.messenger.DataState
import com.example.messenger.R
import com.example.messenger.Utils
import com.example.messenger.chat.ChatViewModel
import com.example.messenger.databinding.ViewImageFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ViewImageFragment : Fragment() {
    private val TAG = "ViewImageFragment"
    val viewModel: ChatViewModel by activityViewModels()
    private var _binding: ViewImageFragmentBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = null
    private var enableImageDownloadMenuItem: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewImageFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.selectedMessage.observe(viewLifecycleOwner) { selectedMessage ->
            if (selectedMessage != null) {
                if (selectedMessage.imageMessageUrl != null) {
                    imageUrl = selectedMessage.imageMessageUrl
                }
            }
        }

        viewModel.imageDownloadState.observe(viewLifecycleOwner) { imageDownloadState ->
            when (imageDownloadState) {
                is DataState.Loading -> {
                    enableImageDownloadMenuItem = false
                    activity?.invalidateOptionsMenu()


                }
                is DataState.Success -> {
                    Log.i(TAG, "onCreateView: ${imageDownloadState.data!!}")
                    Toast.makeText(
                        requireContext(),
                        "Image saved to: ${imageDownloadState.data}",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.doneObservingDownloadState()
                    enableImageDownloadMenuItem = true
                    activity?.invalidateOptionsMenu()


                }
                is DataState.Canceled -> {
                    Toast.makeText(
                        requireContext(),
                        "Operation Canceled",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.doneObservingDownloadState()
                    enableImageDownloadMenuItem = true
                    activity?.invalidateOptionsMenu()


                }

                is DataState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred: ${imageDownloadState.exception.message!!}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i(TAG, "onCreateView: ${imageDownloadState.exception.message!!}")
                    viewModel.doneObservingDownloadState()
                    enableImageDownloadMenuItem = true
                    activity?.invalidateOptionsMenu()


                }

                else -> {

                }
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.image_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.saveImageMenuItem).isEnabled = enableImageDownloadMenuItem
        when (enableImageDownloadMenuItem) {
            true -> {
                menu.findItem(R.id.saveImageMenuItem)
                    .setIcon(R.drawable.ic_baseline_file_download_24)
            }
            false -> {
                menu.findItem(R.id.saveImageMenuItem)
                    .setIcon(R.drawable.ic_baseline_file_download_grey_24)

            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveImageMenuItem -> {
                if (imageUrl != null) {
                    val is29orUp = Utils.isSdkVer29Up {
                        true
                    } ?: false
                    if (EasyPermissions.hasPermissions(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) || is29orUp
                    ) {
                        saveImage()

                    } else {
                        requestWritePermission()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    @AfterPermissionGranted(Constants.WRITE_EXTERNAL_PERMISSION_REQ_CODE)
    private fun saveImage() {
        if (imageUrl != null) {
            viewModel.saveImage(imageUrl!!, requireContext())

        }
    }

    private fun requestWritePermission() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.storage_write_permission_required),
            Constants.WRITE_EXTERNAL_PERMISSION_REQ_CODE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}