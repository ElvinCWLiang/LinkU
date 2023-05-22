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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.linku.databinding.FragmentConversationBinding
import com.project.linku.R
import com.project.linku.ui.utils.Parsefun
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConversationFragment : Fragment() {

    private val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!
    private lateinit var conversationViewModel : ConversationViewModel
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { it ->
            Log.i(TAG, it.toString())
            conversationViewModel.send(it)
        }
    }
    private lateinit var acc : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        conversationViewModel = ViewModelProvider(this).get(ConversationViewModel::class.java)
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation ,container, false)
        val root: View = binding.root
        binding.conversationViewModel = conversationViewModel

        initVariables()
        initView()

        return root
    }

    private fun initVariables() {
        /* receive the conversation remote account from ChatFragment(ChatAdapter) >> */
        val bundle: Bundle? = arguments
        if (bundle != null) {
            Log.i(TAG, "initVariables")
            acc = bundle.getString(resources.getString(R.string.email), "")
            Log.i(TAG, bundle.getString(resources.getString(R.string.email), ""))
            conversationViewModel.syncConversation(acc)
        }
        /* receive the conversation remote account from ChatFragment(ChatAdapter) << */
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        if (savedInstanceState != null) {
            acc = savedInstanceState.getString("email", "")
            Log.i(TAG, "onCreate_acc = $acc")
        }
    }


    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    private fun initView() {
        Log.i(TAG, "initView")
        /* init reyclerview & adapter >> */
        val mConversationAdapter = ConversationAdapter(this, acc)
        binding.recyclerViewConversation.adapter = mConversationAdapter
        binding.recyclerViewConversation.layoutManager = LinearLayoutManager(activity)
        /* init reyclerview & adapter << */

        /* set the imgSelectPicture onClick event >> */
        binding.imgSelectPicture.setOnClickListener {
            pickImages.launch("image/*")
        }
        /* set the imgSelectPicture onClick event << */

        /* receive the data from local repository and insert it into the ConversationAdapter >> */
        conversationViewModel.conversationAdapterMaterial.observe(viewLifecycleOwner) {
            mConversationAdapter.setModelList(it)
            if (it.isNotEmpty()) {
                Log.i(TAG, "size = ${it.size}  it.emfail = ${it[0].email}  type = ${it[0].type}")
                binding.recyclerViewConversation.scrollToPosition(it.size - 1)
            }
            mConversationAdapter.notifyDataSetChanged()
        }
        /* receive the data from local repository and insert it into the ConversationAdapter << */

        /* clear the input message >> */
        conversationViewModel.userMessage.observe(viewLifecycleOwner){
            if(it == "") binding.edtUsercontent.text.clear()
        }
        /* clear the input message << */

        /* navigate the view to voice call or video call when the message has been uploaded to firebase successfully >> */
        conversationViewModel.callStatus.observe(viewLifecycleOwner) {
            it?.let {
                val bundle = Bundle()
                bundle.putString("channel", it.second)
                bundle.putString("email", acc)
                Log.i(TAG, "Type = ${it.first}, Channel = ${it.second}, acc = $acc")
                when (it.first) {
                    2 -> findNavController().navigate(R.id.action_navigation_conversation_to_navigation_voicecall, bundle)
                    3 -> findNavController().navigate(R.id.action_navigation_conversation_to_navigation_videocall, bundle)
                }
            }
        }
        /* navigate the view to voice call or video call when the message has been uploaded to firebase successfully << */

        binding.voicecall.setOnClickListener {
            conversationViewModel.send(type = 2, Parsefun.getInstance().randomStringGenerator())
        }

        binding.videocall.setOnClickListener {
            conversationViewModel.send(type = 3, Parsefun.getInstance().randomStringGenerator())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("email", acc)
    }
}