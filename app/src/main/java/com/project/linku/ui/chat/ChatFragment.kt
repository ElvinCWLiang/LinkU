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
    private val binding get() = _binding!!
    private lateinit var chatViewModel : ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat ,container, false)
        val root: View = binding.root
        binding.chatViewModel = chatViewModel

        initViews(container)

        return root
    }

    private fun initViews(container: ViewGroup?){
        chatViewModel.shouldshowSearchAccountDialog.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                SearchAccountDialog(requireContext(), chatViewModel, it).show()
                binding.edtSearchAccount.text.clear()
            }
        }
        val mChatAdapter = ChatAdapter(this, container)
        binding.recyclerViewChat.adapter = mChatAdapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(activity)
        chatViewModel.syncFriendList()
        binding.layoutChat.visibility = if (MainActivity.islogin) View.VISIBLE else View.INVISIBLE
        chatViewModel.chatAdapterMaterial.observe(viewLifecycleOwner) {
            Log.i(TAG,"size = ${it.size}")
            mChatAdapter.setModelList(it)
            mChatAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}