package com.example.linku.ui.dashboard

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linku.MainActivity
import com.example.linku.MainActivityViewModel
import com.example.linku.R
import com.example.linku.databinding.FragmentDashboardBinding
import com.example.linku.ui.utils.GlideApp
import com.example.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_login.view.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val TAG = "ev_" + javaClass.simpleName

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
        uri?.let { it ->
            Log.i(TAG, it.toString())
            dashboardViewModel.updateAvatar(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.dashboardViewModel = dashboardViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        dashboardViewModel.userAccount.observe(viewLifecycleOwner) {
            binding.btnLogout.isEnabled = it != null
            binding.btnLogin.isEnabled = it == null
            if (it == null) {
                Log.i(TAG, "userAccount null = $it")
                binding.textUsername.text = ""
                LoginDialog(requireContext(), dashboardViewModel).show()
                binding.btnLogout.isEnabled = false
                binding.btnLogin.isEnabled = true
                GlideApp.with(this).load(R.drawable.user).placeholder(R.drawable.cat).into(binding.imgAvatar)
            } else {
                Log.i(TAG, "userAccount = $it")
                binding.textUsername.text = it
                binding.btnLogout.isEnabled = true
                binding.btnLogin.isEnabled = false
                GlideApp.with(this).load(Save.getInstance().getUserAvatarUri(requireContext(), FirebaseAuth.getInstance().currentUser?.email.toString())).placeholder(R.drawable.cat).into(binding.imgAvatar)
            }
        }

        dashboardViewModel.updateRespond.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() }
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}