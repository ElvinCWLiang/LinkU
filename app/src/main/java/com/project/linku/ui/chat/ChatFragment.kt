package com.project.linku.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentChatBinding

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
            it?.let {
                SearchAccountDialog(requireContext(), chatViewModel, it).show()
                binding.edtSearchAccount.text.clear()
            }
        }
        
        val mChatAdapter = ChatAdapter(this, container)
        binding.recyclerViewChat.adapter = mChatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(activity)

        chatViewModel.syncFriendList()

        if (MainActivity.islogin) {
            binding.layoutChat.visibility = View.VISIBLE
        } else {
            binding.layoutChat.visibility = View.INVISIBLE
        }

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