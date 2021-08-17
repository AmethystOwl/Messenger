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
@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private lateinit var searchAdapter: UsersAdapter
    private lateinit var messagesAdapter: MessagesAdapter


    override fun onStart() {
        super.onStart()
        setupAdapters()

        messagesAdapter.startListening()
        searchAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.stopListening()
        searchAdapter.stopListening()
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
        // TODO : add states to viewModel and observe : searchView state, search query etc..


        viewModel.messagesQueryState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // TODO : Show loading progressbar
                    Log.i(TAG, "onCreateView: message queue state : loading")
                }
                is DataState.Success -> {
                    val messagesOptions = FirestoreRecyclerOptions.Builder<MessageModel>()
                        .setQuery(it.data, MessageModel::class.java)
                        .build()
                    messagesAdapter.updateOptions(messagesOptions)

                    messagesAdapter.notifyDataSetChanged()
                    if (messagesAdapter.itemCount == 0) {
                        binding.noMessagesTv.visibility = View.VISIBLE
                    }
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
                is DataState.Canceled -> {

                }
                else -> {

                }
            }
        }
        viewModel.usersQueryState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // TODO : Show loading progressbar
                    Log.i(TAG, "onCreateView: user queue state : loading")
                }
                is DataState.Success -> {
                    val userOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
                        .setQuery(it.data, UserProfile::class.java)
                        .build()
                    searchAdapter.updateOptions(userOptions)
                    searchAdapter.notifyDataSetChanged()


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
                is DataState.Canceled -> {

                }
                else -> {

                }
            }
        }
        viewModel.friendAdditionState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // TODO : Show loading animation on button
                }
                is DataState.Success -> {
                    view?.showSnackbar(
                        binding.coordinator,
                        "Friend Added",
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )


                }
                is DataState.Canceled -> {
                    view?.showSnackbar(
                        binding.coordinator,
                        "Operation canceled",
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
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


    private fun setupAdapters() {
        val defaultUessageQuery = viewModel.getDefaultMessageQuery()
        val defaultMessagesOptions = FirestoreRecyclerOptions.Builder<MessageModel>()
            .setQuery(defaultUessageQuery, MessageModel::class.java)
            .build()
        messagesAdapter = MessagesAdapter(defaultMessagesOptions)
        binding.messagesRecyclerview.adapter = messagesAdapter

        val defaultUserQuery = viewModel.getDefaultUserQuery()
        val defaultUserOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
            .setQuery(defaultUserQuery, UserProfile::class.java)
            .build()
        searchAdapter = UsersAdapter(defaultUserOptions, onUserClickListener)


        binding.searchRecyclerview.adapter = searchAdapter

        if (messagesAdapter.itemCount == 0) {
            binding.noMessagesTv.visibility = View.VISIBLE
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        val onActionExpandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                showSearchLayout(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                showSearchLayout(false)
                return true
            }

        }

        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.onActionViewExpanded()
        menu.findItem(R.id.app_bar_search).setOnActionExpandListener(onActionExpandListener)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        })

        searchView.queryHint = getString(R.string.search_hint)
        super.onCreateOptionsMenu(menu, inflater)
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

    private fun showSearchLayout(bShow: Boolean) {
        when (bShow) {
            true -> {
                binding.messagesRecyclerview.visibility = View.INVISIBLE
                binding.noMessagesTv.visibility = View.INVISIBLE
                binding.searchRecyclerview.visibility = View.VISIBLE
            }
            false -> {
                binding.searchRecyclerview.visibility = View.INVISIBLE
                binding.messagesRecyclerview.visibility = View.VISIBLE
                binding.noMessagesTv.visibility = View.VISIBLE
            }
        }
    }

    @ExperimentalCoroutinesApi
    private val onUserClickListener = UsersAdapter.OnUserClickListener { email ->
        viewModel.addFriendByEmail(email)
    }

}
