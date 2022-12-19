package com.jamshedalamqaderi.anview

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.jamshedalamqaderi.anview.ui.theme.AnViewTheme

class MainActivity : ComponentActivity() {
    companion object {
        var screenWidth: Int = 0
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager.defaultDisplay
        setContent {
            AnViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val isAccessibilityServiceEnabled = remember {
                        mutableStateOf(
                            ScraperAccessibilityService.isEnabled(
                                this@MainActivity
                            )
                        )
                    }
                    OnLifecycleEvent { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> {
                                isAccessibilityServiceEnabled.value =
                                    ScraperAccessibilityService.isEnabled(this@MainActivity)
                            }
                            else -> {}
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).also {
                                startActivity(it)
                            }
                        }) {
                            Text(text = "${if (isAccessibilityServiceEnabled.value) "Disable" else "Enable"} Accessibility")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
        val eventHandler = rememberUpdatedState(onEvent)
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

        DisposableEffect(lifecycleOwner.value) {
            val lifecycle = lifecycleOwner.value.lifecycle
            val observer = LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, event)
            }

            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }
}
