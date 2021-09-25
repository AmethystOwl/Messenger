package com.example.messenger

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.messenger.Utils.Companion.showSnackbar
import com.example.messenger.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private lateinit var navController: NavController

    // TODO : Add option menu & search button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.loginFragment, R.id.registerFragment, R.id.chatsFragment, R.id.friendsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // TODO : save things in local db...
        setupBottomNavMenu(navController)


    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.bottomNavView.setupWithNavController(navController)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.permissionPermanentlyDenied(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) || EasyPermissions.permissionPermanentlyDenied(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) || EasyPermissions.permissionPermanentlyDenied(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            AppSettingsDialog.Builder(this).setTitle(getString(R.string.permission_required))
                .build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.i(TAG, "onRationaleAccepted: $requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        binding.root.showSnackbar(
            binding.root,
            "Permission permanently denied, click OK to go to settings and allow manually",
            Snackbar.LENGTH_INDEFINITE,
            "ok"
        ) {
            AppSettingsDialog.Builder(this).build().show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.app_bar_search)?.isVisible = false
        menu?.findItem(R.id.sign_out_option)?.isVisible = false

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}