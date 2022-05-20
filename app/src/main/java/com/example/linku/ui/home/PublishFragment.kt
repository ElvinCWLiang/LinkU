package com.example.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.linku.R
import com.example.linku.databinding.FragmentPublishBinding

class PublishFragment: Fragment() {
    private var _binding: FragmentPublishBinding? = null
    private val binding get() = _binding!!
    var articleId: String? = null
    private val TAG = "ev_" + javaClass.simpleName

    // androidx.fragment.app.Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val publishViewModel = ViewModelProvider(this).get(PublishViewModel::class.java)
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_publish, container, false)

        val root: View = binding.root

        binding.publishViewModel = publishViewModel

        publishViewModel.publishResponse.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "publish success", Toast.LENGTH_SHORT)
                findNavController().navigate(R.id.navigation_home)
            } else {
                Toast.makeText(requireContext(), "can't publish", Toast.LENGTH_SHORT)
            }
        }

        return root
    }

}