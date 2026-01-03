package com.dockflow

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dockflow.service.MediaSessionService
import com.dockflow.ui.screens.DockModeScreen
import com.dockflow.ui.screens.PermissionScreen
import com.dockflow.ui.theme.DockFlowTheme
import com.dockflow.ui.theme.ThemeManager
import com.dockflow.ui.theme.ThemeType
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize theme manager
        themeManager = ThemeManager(this)
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Hide system bars for immersive experience
        hideSystemBars()
        
        setContent {
            var currentTheme by remember { mutableStateOf(ThemeType.NEON) }
            val hasPermission = remember { mutableStateOf(checkNotificationPermission()) }
            
            // Observe theme changes
            LaunchedEffect(Unit) {
                themeManager.themeFlow.collect { theme ->
                    currentTheme = theme
                }
            }
            
            // Check permission periodically
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(1000)
                    hasPermission.value = checkNotificationPermission()
                }
            }
            
            DockFlowTheme(themeType = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasPermission.value) {
                        DockModeScreen(
                            themeManager = themeManager,
                            currentTheme = currentTheme
                        )
                    } else {
                        PermissionScreen()
                    }
                }
            }
        }
    }
    
    /**
     * Check if notification listener permission is granted
     */
    private fun checkNotificationPermission(): Boolean {
        val packageName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        
        if (flat != null && flat.isNotEmpty()) {
            val names = flat.split(":")
            for (name in names) {
                val componentName = ComponentName.unflattenFromString(name)
                if (componentName != null) {
                    if (packageName == componentName.packageName) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    /**
     * Hide system bars for immersive experience
     */
    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    
    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }
}
