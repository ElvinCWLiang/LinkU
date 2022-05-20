package com.example.linku

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.linku.data.local.UserModel
import com.example.linku.databinding.ActivityMainBinding
import com.example.linku.ui.utils.Save
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val TAG = "ev_" + javaClass.simpleName
    private lateinit var binding: ActivityMainBinding

    companion object{
        lateinit var userkeySet : HashMap<String, UserModel>
        var islogin = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                if (!it) { AlertDialog.Builder(this).setMessage("Please login").create().show() }
            }
        }
        /* observe the login status status << */

        initData(mainactivityViewModel)
    }

    private fun initData(mainActivityViewModel: MainActivityViewModel){
        userkeySet = mainActivityViewModel.syncLocalUserData()

    }
}