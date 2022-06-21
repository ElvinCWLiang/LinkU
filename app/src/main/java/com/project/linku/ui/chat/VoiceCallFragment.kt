package com.project.linku.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentChatBinding
import com.project.linku.databinding.FragmentVoicecallBinding
import com.project.linku.ui.utils.GlideApp
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.android.synthetic.main.fragment_conversation.*

class VoiceCallFragment : Fragment() {

    val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentVoicecallBinding? = null
    private val binding get() = _binding!!
    private lateinit var voicecallViewModel : VoiceCallViewModel
    private lateinit var acc : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        voicecallViewModel =
            ViewModelProvider(this).get(VoiceCallViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voicecall ,container, false)
        val root: View = binding.root
        binding.voicecallViewModel = voicecallViewModel

        val bundle: Bundle? = arguments
        bundle?.let {
            val channel = bundle.getString("channel", "")
            acc = bundle.getString("email", "")
            Log.i(TAG, "channel = $channel, acc = $acc")
            voicecallViewModel.initializeAndJoinChannel(channel)
        }

        voicecallViewModel.statusDialog.observe(viewLifecycleOwner) { status ->
            val bundle = Bundle()
            bundle.putString("email", acc)
            Log.i(TAG, "acc = $acc, status = $status")
            if (status == "endCall" || status == "UserOffline") {
                findNavController().navigate(R.id.action_navigation_voicecall_to_navigation_conversation, bundle)
            }
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
        }

        GlideApp.with(this).load(MainActivity.userkeySet.get(acc)?.useruri).placeholder(R.drawable.cat).circleCrop().into(binding.imgCallerAvatar)
        binding.txvCallerName.text = acc

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        voicecallViewModel.endCall()
    }
}