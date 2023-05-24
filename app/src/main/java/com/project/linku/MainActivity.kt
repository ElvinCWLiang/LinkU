package com.project.linku

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.project.linku.data.local.UserModel
import com.project.linku.databinding.ActivityMainBinding
import com.project.linku.ui.utils.Save
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
// Kotlin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {}
        val mainViewModel =
            ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding.mainactivityViewModel = mainViewModel

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navView: BottomNavigationView = binding.navView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        lifecycleScope.launch {
            /* observe the network connection status >> */
            launch {
                mainViewModel.isConnected().collectLatest {
                    if (it) Save.saveConnectionStatus(this@MainActivity, it)
                    else Toast.makeText(this@MainActivity, "Disconnect", Toast.LENGTH_SHORT).show()
                }
            }
            /* observe the network connection status << */

            /* observe the login status >> */
            launch {
                mainViewModel.isLogin().collectLatest {
                    islogin = it
                    startService(Intent(this@MainActivity, MessageService::class.java))
                    if (!islogin) {
                        Toast.makeText(this@MainActivity, "Please check network and login status", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            /* observe the login status status << */
        }
        initData(mainViewModel)
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

    companion object{
        lateinit var userkeySet : HashMap<String, UserModel>
        var islogin = false
    }
}