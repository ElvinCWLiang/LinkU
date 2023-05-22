package com.project.linku.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {
    val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatViewModel : ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel =
            ViewModelProvider(this)[ChatViewModel::class.java]

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat ,container, false)
        val root: View = binding.root

        initViews()

        return root
    }

    private fun initViews() {
        binding.btnSearchAccount.setOnClickListener {
            lifecycleScope.launch {
                chatViewModel.searchAccount(binding.edtSearchAccount.text.toString()).collectLatest {
                    SearchAccountDialog(requireContext(),
                        account = it,
                        friendList = chatViewModel.friendModelList.value,
                        addFriend = { account -> chatViewModel.addFriend(account) },
                        showMessage = { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }).show()
                    binding.edtSearchAccount.text.clear()
                }
            }
        }

        val chatAdapter = ChatAdapter {
            findNavController().navigate(R.id.action_navigation_chat_to_navigation_conversation, it)
        }

        binding.recyclerViewChat.adapter = chatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(activity)
        chatViewModel.syncFriendList()
        binding.layoutChat.visibility = if (MainActivity.islogin) View.VISIBLE else View.INVISIBLE

        lifecycleScope.launch {
            chatViewModel.friendModelList.collectLatest {
                chatAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}