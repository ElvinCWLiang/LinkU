package com.project.linku.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.linku.R
import com.project.linku.databinding.FragmentPublishBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PublishFragment: Fragment() {
    private var _binding: FragmentPublishBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ev_" + javaClass.simpleName
    private lateinit var publishViewModel : PublishViewModel

    // androidx.fragment.app.Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        publishViewModel = ViewModelProvider(this)[PublishViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_publish, container, false)
        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.btnPublishSubmit.setOnClickListener {
            lifecycleScope.launch {
                publishViewModel.publishArticle(
                    board = it.resources.getStringArray(R.array.publish_array)[binding.spnPublishBoard.selectedItemPosition],
                    title = binding.edtPublishTitle.text.toString(),
                    content = binding.edtPublishContent.text.toString()
                ).collectLatest {
                    if (it) {
                        Toast.makeText(requireContext(), resources.getString(R.string.publish_success), Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_home)
                    } else {
                        Toast.makeText(requireContext(), resources.getString(R.string.publish_fail), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        /* set the imgSelectPicture onClick event >> */
        binding.imgSelectPicture.setOnClickListener {
            pickImages().launch("image/*")
        }
        /* set the imgSelectPicture onClick event << */
    }

    private fun pickImages() = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { imagePath ->
            Log.i(TAG, imagePath.toString())
            lifecycleScope.launch {
                publishViewModel.send(imagePath).collectLatest {
                    if (it) {
                        Toast.makeText(requireContext(), resources.getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), resources.getString(R.string.update_fail), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}