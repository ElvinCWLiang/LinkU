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
import com.example.linku.databinding.FragmentConversationBinding
import com.example.linku.R

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

        /* receive the data from local repository and insert it into the ConversationAdapter >> */
        conversationViewModel.conversationAdapterMaterial.observe(viewLifecycleOwner) {
            Log.i(TAG,"size = ${it.size}  it.email = ${it[0].email}  type = ${it[0].type}")
            mConversationAdapter.setModelList(it)
            mConversationAdapter.notifyDataSetChanged()
            if (it.isNotEmpty()) binding.recyclerViewConversation.smoothScrollToPosition(it.size - 1)
        }
        /* receive the data from local repository and insert it into the ConversationAdapter << */

        /* receive the conversation remote account from ChatFragment(ChatAdapter) >> */
        val bundle: Bundle? = arguments
        if (bundle != null) {
            val acc = bundle.getString("email", "")
            Log.i(TAG, bundle.getString("email", ""))
            conversationViewModel.syncConversation(acc)
        }
        /* receive the conversation remote account from ChatFragment(ChatAdapter) << */

        /* clear the input message >> */
        conversationViewModel.userMessage.observe(viewLifecycleOwner){
            if(it == "") binding.edtUsercontent.text.clear()
        }
        /* clear the input message << */

        return root
    }
}