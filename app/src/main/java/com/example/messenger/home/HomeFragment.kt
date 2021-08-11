package com.example.messenger.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.*
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.HomeFragmentBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var messagesAdapter: MessagesAdapter


    override fun onStart() {
        super.onStart()
        // messagesAdapter.startListening()
        usersAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        // messagesAdapter.stopListening()
        usersAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupAdapter()


        viewModel.queryState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // TODO : Show loading progressbar
                    Log.i(TAG, "onCreateView: queue state : loading")
                }
                is DataState.Success -> {
                    val filteredUserOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
                        .setQuery(it.data, UserProfile::class.java)
                        .build()
                    usersAdapter.updateOptions(filteredUserOptions)
                    usersAdapter.notifyDataSetChanged()

                }
                is DataState.Error -> {
                    view?.showSnackbar(
                        binding.coordinator,
                        it.exception.message!!,
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
            }
        }

        setHasOptionsMenu(true)
        return binding.root

    }

    private fun setupAdapter() {
        val query = viewModel.getDefaultUserQuery()
        val userOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
            .setQuery(query, UserProfile::class.java)
            .build()
        usersAdapter = UsersAdapter(userOptions)
        binding.recyclerview.adapter = usersAdapter
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        val onActionExpandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                view?.showSnackbar(
                    binding.coordinator,
                    "Expanded",
                    Snackbar.LENGTH_LONG,
                    null,
                    null
                )
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                view?.showSnackbar(
                    binding.coordinator,
                    "Collapsed",
                    Snackbar.LENGTH_LONG,
                    null,
                    null
                )
                return true

            }

        }
        menu.findItem(R.id.app_bar_search).setOnActionExpandListener(onActionExpandListener)
        val searchQuery = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.defaultUserQuery()
                } else {
                    viewModel.filterUserQuery(newText)
                }
                return true
            }

        }
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(searchQuery)
        searchView.queryHint = getString(R.string.search_hint)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_option -> {
                viewModel.signOut()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                super.onOptionsItemSelected(item)
            }
            R.id.app_bar_search -> {

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
