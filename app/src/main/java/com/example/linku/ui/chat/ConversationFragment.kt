package com.example.linku.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.R
import com.example.linku.databinding.FragmentConversationBinding

class ConversationFragment : Fragment() {

    private val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val conversationViewModel =
            ViewModelProvider(this).get(ConversationViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation ,container, false)
        val root: View = binding.root

        binding.conversationViewModel = conversationViewModel

        val mConversationAdapter = ConversationAdapter(this, container)
        binding.recyclerViewConversation.adapter = mConversationAdapter
        binding.recyclerViewConversation.layoutManager = LinearLayoutManager(activity)
/*
        conversationViewModel.ConversationAdapterMaterial().observe(viewLifecycleOwner){

        }
*/
        val bundle: Bundle? = arguments
        if (bundle != null) {
            val acc = bundle.getString("email", "")
            Log.i(TAG, bundle.getString("email", ""))
            conversationViewModel.syncConversation(acc)
        }

        conversationViewModel.userMessage.observe(viewLifecycleOwner){
            binding.edtUsercontent.text.clear()
        }


        val mChatAdapter = ChatAdapter(this, container)
        binding.recyclerViewConversation.adapter = mChatAdapter
        binding.recyclerViewConversation.layoutManager = LinearLayoutManager(activity)

        conversationViewModel.conversationAdapterMaterial.observe(viewLifecycleOwner) {

        }

        /*

        chatViewModel.syncFriendList()

        chatViewModel.chatAdapterMaterial.observe(viewLifecycleOwner) {
            Log.i(TAG,"size = ${it.size}")
            mChatAdapter.setModelList(it)
            mChatAdapter.notifyDataSetChanged()
        }
        */



        return root
    }
}