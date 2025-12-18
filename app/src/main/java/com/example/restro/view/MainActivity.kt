package com.example.restro.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.restro.R
import com.example.restro.data.model.SocketNotification
import com.example.restro.databinding.ActivityMainBinding
import com.example.restro.utils.AuthEvent
import com.example.restro.utils.AuthEventBus
import com.example.restro.utils.Utilities.showNotificationPopup
import com.example.restro.view.notification.NotificationDetailsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            })
        if (!hasAllPermissions(
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            )
        ) {
            requestAppPermissions()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun requestAppPermissions() {
        val requiredPermissions = mutableListOf<String>()

        // Notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)

        // Full-screen intent (Android 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) requiredPermissions.add(
            Manifest.permission.USE_FULL_SCREEN_INTENT
        )

        // Add any additional permissions your app needs
        requiredPermissions.addAll(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            )
        )

        // Filter out already granted permissions
        val permissionsToRequest = requiredPermissions.filterNot {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, permissionsToRequest.toTypedArray(), 1001
            )
        }
    }


    private fun hasAllPermissions(permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            val deniedPermissions = mutableListOf<String>()
            val grantedPermissions = mutableListOf<String>()

            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) grantedPermissions.add(
                    permission
                )
                else deniedPermissions.add(permission)
            }

            when {
                deniedPermissions.isEmpty() -> {
                    Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
                }

                grantedPermissions.isNotEmpty() -> {
                    Toast.makeText(
                        this,
                        "Some permissions granted: ${grantedPermissions.size}, denied: ${deniedPermissions.size}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(this, "All permissions denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Exit App")
            .setMessage("Are you sure you want to exit?").setPositiveButton("Exit") { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.show()
    }

    private val socketDialogReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            val notification =
                intent?.getSerializableExtra("notification", SocketNotification::class.java)

            Timber.d("Message:${Gson().toJson(notification)}")

            showNotificationPopup(notification?.title!!, notification.body!!) {
                startActivity(
                    Intent(
                        this@MainActivity, NotificationDetailsActivity::class.java
                    ).apply {
                        putExtra("notification", notification)
                    })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(binding.root.context)
            .registerReceiver(socketDialogReceiver, IntentFilter("SHOW_SOCKET_DIALOG"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(binding.root.context)
            .unregisterReceiver(socketDialogReceiver)
    }

}