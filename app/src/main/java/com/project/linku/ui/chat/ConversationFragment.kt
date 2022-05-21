package com.project.linku.ui.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.linku.databinding.FragmentConversationBinding
import com.project.linku.R

class ConversationFragment : Fragment() {

    private val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!
    private lateinit var conversationViewModel : ConversationViewModel
    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { it ->
            Log.i(TAG, it.toString())
            conversationViewModel.send(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        conversationViewModel = ViewModelProvider(this).get(ConversationViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation ,container, false)
        val root: View = binding.root

        binding.conversationViewModel = conversationViewModel

        val mConversationAdapter = ConversationAdapter(this, container)
        binding.recyclerViewConversation.adapter = mConversationAdapter
        binding.recyclerViewConversation.layoutManager = LinearLayoutManager(activity)

        /* receive the data from local repository and insert it into the ConversationAdapter >> */
        conversationViewModel.conversationAdapterMaterial.observe(viewLifecycleOwner) {
            mConversationAdapter.setModelList(it)
            mConversationAdapter.notifyDataSetChanged()
            if (it.isNotEmpty()) {
                Log.i(TAG, "size = ${it.size}  it.email = ${it[0].email}  type = ${it[0].type}")
                binding.recyclerViewConversation.smoothScrollToPosition(it.size - 1)
            }
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

        binding.imgSelectPicture.setOnClickListener {
            pickImages.launch("image/*")
        }

        return root
    }
}