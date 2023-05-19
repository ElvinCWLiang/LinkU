package com.project.linku

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.project.linku.data.local.UserModel
import com.project.linku.databinding.ActivityMainBinding
import com.project.linku.ui.utils.Save
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
// Kotlin
import io.agora.rtc.RtcEngine
import io.agora.rtc.IRtcEngineEventHandler

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "ev_" + javaClass.simpleName
    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    companion object{
        lateinit var userkeySet : HashMap<String, UserModel>
        var islogin = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {}
        val mainactivityViewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.mainactivityViewModel = mainactivityViewModel

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navView: BottomNavigationView = binding.navView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mainactivityViewModel.isLogin()
        mainactivityViewModel.isConnected()

        /* observe the network connecting status >> */
        mainactivityViewModel.isConnected.observe(this) {
            if (it) Save.getInstance().saveConnectionStatus(this, it)
            else AlertDialog.Builder(this).setMessage("network fail").create().show()
        }
        /* observe the network connecting status << */

        /* observe the login status >> */
        mainactivityViewModel.isLogin.observe(this) {
            it?.let {
                islogin = it
                startService(Intent(this, MessageService::class.java))
                if (!islogin) {
                    AlertDialog.Builder(this).setMessage("Please check network and login status").create().show()
                }
            }
        }
        /* observe the login status status << */
        val c : ArrayList<String> = ArrayList<String>()

        initData(mainactivityViewModel)

        when (true) {
            true -> System.out.println()
            false -> System.out.println()
        }

    }
    private fun initData(mainActivityViewModel: MainActivityViewModel){
        userkeySet = mainActivityViewModel.syncLocalUserData()
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }
}