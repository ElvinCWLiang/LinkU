package com.project.linku.ui.dashboard

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentDashboardBinding
import com.project.linku.ui.utils.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_login.view.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val TAG = "ev_" + javaClass.simpleName
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { it ->
            Log.i(TAG, it.toString())
            dashboardViewModel.updateAvatar(it)
        }
    }
    lateinit var dialog : LoginDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        initView(container)

        return binding.root
    }

    private fun initView(container: ViewGroup?) {
        dialog = LoginDialog(requireContext(), dashboardViewModel)

        binding.dashboardViewModel = dashboardViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        dashboardViewModel.userAccount.observe(viewLifecycleOwner) { account ->
            binding.btnLogout.isEnabled = account != null
            binding.btnLogin.isEnabled = account == null

            if (account == null || account == "null") {
                binding.layoutDashboardSetting.visibility = View.INVISIBLE
                binding.textUsername.text = ""
                dialog.show()
                binding.btnLogout.isEnabled = false
                binding.btnLogin.isEnabled = true
                GlideApp.with(this).load(R.drawable.cat).circleCrop().into(binding.imgAvatar)
            } else {
                binding.layoutDashboardSetting.visibility = View.VISIBLE
                binding.textUsername.text = account
                binding.btnLogout.isEnabled = true
                binding.btnLogin.isEnabled = false
                Log.i(TAG, "${MainActivity.userkeySet.get(account)?.useruri}")
                GlideApp.with(this).load(MainActivity.userkeySet.get(account)?.useruri).circleCrop().placeholder(R.drawable.cat).into(binding.imgAvatar)
            }
            Log.i(TAG, "userAccount = $account")
        }

        dashboardViewModel.updateRespond.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.imgAvatar.setOnClickListener {
            pickImages.launch("image/*")
        }

        dashboardViewModel.isAvatarChanged.observe(viewLifecycleOwner) {
            it?.let { GlideApp.with(this).load(it).placeholder(R.drawable.cat).into(binding.imgAvatar) }
        }

        dashboardViewModel.introduction.observe(viewLifecycleOwner) {
            it?.let { binding.txvIntroduction.text = it}
        }

        binding.txvIntroduction.setOnClickListener {
            val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_introduction, container, false)
            AlertDialog.Builder(requireContext()).setView(view).setMessage("Input your introduction : ").setPositiveButton("set")
            { dialog, which ->
                Log.i(TAG, "text = ${view.edt_introduction.text}")
                dashboardViewModel.updateUserIntroduction(view.edt_introduction.text.toString())
            }.create().show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialog.dismiss()
    }
}