package com.example.messenger.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.DataState
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.SharedViewModel
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.adapter.UsersAdapter
import com.example.messenger.databinding.ChatsFragmentBinding
import com.example.messenger.model.UserProfile
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChatsFragment : Fragment() {
    private val TAG = "HomeFragment"
    private val sharedViewModel: SharedViewModel by viewModels()
    private val chatsViewModel: ChatsViewModel by viewModels()

    private lateinit var binding: ChatsFragmentBinding
    private lateinit var searchAdapter: UsersAdapter

    // private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var mainActivity: MainActivity


    override fun onStart() {
        super.onStart()
        setupAdapters()
        mainActivity = activity as MainActivity
        mainActivity.binding.bottomNavView.visibility = View.VISIBLE

        //  conversationAdapter.startListening()
        searchAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mainActivity.binding.bottomNavView.visibility = View.INVISIBLE

        //  conversationAdapter.stopListening()
        searchAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = chatsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // TODO : Change searchView to make it search Chats, not People, add another Fragment to
        //        handle that

        // TODO : Network states :(
        // TODO : Add States to viewModel and observe : searchView state, search query etc..
        // TODO : When friend added, remove add button, at startup remove add button for friends in search list.
        chatsViewModel.messagesQueryState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    // TODO : Show loading progressbar
                    Log.i(TAG, "onCreateView: message queue state : loading")
                }
                is DataState.Success -> {
                    //  val messagesOptions = FirestoreRecyclerOptions.Builder<Conversation>()
                    //     .setQuery(it.data, Conversation::class.java)
                    //     .build()
                    //   conversationAdapter.updateOptions(messagesOptions)
                    // conversationAdapter.notifyDataSetChanged()
                    // if (conversationAdapter.itemCount == 0) {
                    //     binding.noMessagesTv.visibility = View.VISIBLE
                    // }
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


    private fun setupAdapters() {
        /* val defaultMessageQuery = chatsViewModel.getDefaultMessageQuery()
         val defaultMessagesOptions = FirestoreRecyclerOptions.Builder<Conversation>()
             .setQuery(defaultMessageQuery, Conversation::class.java)
             .build()
         conversationAdapter = ConversationAdapter(defaultMessagesOptions)
         binding.messagesRecyclerview.adapter = conversationAdapter
 */
        val defaultUserQuery = sharedViewModel.getDefaultUserQuery()
        val defaultUserOptions = FirestoreRecyclerOptions.Builder<UserProfile>()
            .setQuery(defaultUserQuery, UserProfile::class.java)
            .build()
        searchAdapter = UsersAdapter(defaultUserOptions, onUserClickListener)
        binding.searchRecyclerview.adapter = searchAdapter

        /* if (conversationAdapter.itemCount == 0) {
             binding.noMessagesTv.visibility = View.VISIBLE
         }*/
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.app_bar_search)?.isVisible = true
        menu.findItem(R.id.sign_out_option)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

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
        sharedViewModel.addFriendByEmail(email)
    }

}
