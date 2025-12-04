package engineer.filip.hoarder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import engineer.filip.hoarder.data.ShareHandler
import engineer.filip.hoarder.navigation.NavGraph
import engineer.filip.hoarder.theme.HoarderTheme
import engineer.filip.hoarder.ui.Hints
import javax.inject.Inject

/**
 * Day 2 Exercise 7: Handle share intents from other apps.
 *
 * Inject ShareHandler, handle intent in onCreate, override onNewIntent.
 *
 * Stuck? See Hints.Day2Exercise7
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // TODO: Inject ShareHandler
    @Inject
    lateinit var shareHandler: ShareHandler

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO: Handle share intent
        handleShareIntent(intent)

        Log.d("MainActivity", "BuildConfig.API_URL: ${BuildConfig.API_URL}")

        setContent {
            HoarderTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // TODO: Override onNewIntent
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent: ${intent.getStringExtra(Intent.EXTRA_TEXT)}")
        handleShareIntent(intent)
    }

    // TODO: Implement handleShareIntent
    private fun handleShareIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type != "text/plain") return
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                Log.d("MainActivity", "shared text, ${intent.getStringExtra(Intent.EXTRA_TEXT)}")
                shareHandler.onShareReceived(text = sharedText)
            }

            Intent.ACTION_VIEW -> {
                val bookmarkId = intent.data.toString().toUri().lastPathSegment
                shareHandler.onDeeplinkReceived(bookmarkId)
            }
        }
    }

    @Suppress("unused")
    private val _hint = Hints.Day2Exercise7
}
