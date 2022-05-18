package com.example.linku.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linku.R
import com.example.linku.databinding.FragmentChatBinding
import com.example.linku.ui.chat.ChatViewModel
import com.example.linku.ui.home.HomeAdapter

class ChatFragment : Fragment() {

    val TAG = "ev_" + javaClass.simpleName

    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat ,container, false)
        val root: View = binding.root

        binding.chatViewModel = chatViewModel

        chatViewModel.shouldshowSearchAccountDialog.observe(viewLifecycleOwner) {
            Log.i(TAG,"it = $it")
            it?.let {
                SearchAccountDialog(requireContext(), chatViewModel, it).show()
            }
        }
        val mChatAdapter = ChatAdapter(this, container)
        binding.recyclerViewChat.adapter = mChatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(activity)

        chatViewModel.syncFriendList()

        chatViewModel.chatAdapterMaterial.observe(viewLifecycleOwner) {
            Log.i(TAG,"size = ${it.size}")
            mChatAdapter.setModelList(it)
            mChatAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}