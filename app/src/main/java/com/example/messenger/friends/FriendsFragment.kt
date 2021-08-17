package com.example.messenger.friends

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
import com.example.messenger.chats.ChatsFragmentDirections
import com.example.messenger.databinding.FriendsFragmentBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FriendsFragment : Fragment() {
    private val TAG = "FriendsFragment"

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FriendsFragmentBinding

    private val friendsViewModel: FriendsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var searchAdapter: UsersAdapter
    private lateinit var friendsAdapter: FriendsAdapter
    override fun onStart() {
        super.onStart()
        setupAdapters()

        mainActivity = activity as MainActivity
        mainActivity.binding.bottomNavView.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        mainActivity.binding.bottomNavView.visibility = View.INVISIBLE

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FriendsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = friendsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        friendsViewModel.friendsList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    Log.i(TAG, "onCreateView: FRIENDS : ${it.data}")
                    if (it.data.isNotEmpty()) {
                        binding.friendsRecyclerview.visibility = View.VISIBLE
                        binding.noFriendsTv.visibility = View.GONE

                    } else {
                        binding.friendsRecyclerview.visibility = View.GONE
                        binding.noFriendsTv.visibility = View.VISIBLE

                    }


                    friendsAdapter.submitList(it.data)
                    friendsAdapter.notifyDataSetChanged()
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
                DataState.Loading -> {
                    Log.i(TAG, "onCreateView: Loading")
                }
                else -> {

                }
            }

        }

        sharedViewModel.usersQueryState.observe(viewLifecycleOwner) {
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
                    view?.showSnackbar(
                        binding.coordinator,
                        "Operation Canceled",
                        Snackbar.LENGTH_LONG,
                        null,
                        null
                    )
                }
                else -> {

                }
            }
        }
        sharedViewModel.friendAdditionState.observe(viewLifecycleOwner) {
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.app_bar_search)?.isVisible = true
        menu.findItem(R.id.sign_out_option)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_option -> {
                sharedViewModel.signOut()
                findNavController().navigate(ChatsFragmentDirections.actionChatsFragmentToLoginFragment())
                super.onOptionsItemSelected(item)
            }
            R.id.app_bar_search -> {
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

                val searchView = item.actionView as SearchView
                searchView.onActionViewExpanded()
                item.setOnActionExpandListener(onActionExpandListener)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) {
                            sharedViewModel.defaultUserQuery()
                        } else {
                            sharedViewModel.filterUserQuery(newText)
                        }
                        return true
                    }

                })

                searchView.queryHint = getString(R.string.search_hint)
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchLayout(bShow: Boolean) {
        when (bShow) {
            true -> {
                binding.friendsRecyclerview.visibility = View.INVISIBLE
                binding.searchRecyclerview.visibility = View.VISIBLE
            }
            false -> {
                binding.searchRecyclerview.visibility = View.INVISIBLE
                binding.friendsRecyclerview.visibility = View.VISIBLE
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupAdapters() {
        friendsAdapter = FriendsAdapter(onFriendClickListener)

        binding.friendsRecyclerview.adapter = friendsAdapter

        val defaultUserQuery = sharedViewModel.getDefaultUserQuery()
        val defaultUserOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
            .setQuery(defaultUserQuery, UserProfile::class.java)
            .build()
        searchAdapter = UsersAdapter(defaultUserOptions, onUserClickListener)
        binding.searchRecyclerview.adapter = searchAdapter

        if (friendsAdapter.itemCount == 0) {
            binding.noFriendsTv.visibility = View.VISIBLE
            binding.friendsRecyclerview.visibility = View.INVISIBLE
        } else {
            binding.noFriendsTv.visibility = View.INVISIBLE
            binding.friendsRecyclerview.visibility = View.VISIBLE
        }
    }

    @ExperimentalCoroutinesApi
    val onFriendClickListener = FriendsAdapter.OnUserClickListener {
        // TODO : create chat fragment, from uid(or email) to friend uid(or email)
    }

    @ExperimentalCoroutinesApi
    val onUserClickListener = UsersAdapter.OnUserClickListener {
        sharedViewModel.filterUserQuery(it)
    }
}