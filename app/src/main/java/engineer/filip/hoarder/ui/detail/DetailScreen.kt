package engineer.filip.hoarder.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.ui.Hints

/**
 * Screen-level composable for bookmark detail.
 * Handles ViewModel and navigation events.
 */
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDeleteConfirmation: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observe one-time events for navigation
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.NavigateBack -> onNavigateBack()
                is DetailEvent.NavigateToDeleteConfirmation -> onNavigateToDeleteConfirmation(event.bookmarkId)
            }
        }
    }

    DetailContent(
        state = uiState,
        onAction = viewModel::onAction
    )
}

/**
 * Pure UI composable for detail screen.
 *
 * Day 2 Exercise 3: Add "Open in Browser" button. See Hints.Day2Exercise3
 * Day 2 Exercise 14: Polish detail screen. See Hints.Day2Exercise14
 * Day 1 Exercise 16: Add TextField to edit notes. See Hints.Exercise16
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    state: DetailUiState,
    onAction: (DetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.bookmark?.title ?: "Bookmark") },
                navigationIcon = {
                    IconButton(onClick = { onAction(DetailAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (state.bookmark != null) {
                        IconButton(onClick = { onAction(DetailAction.DeleteClick) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete bookmark"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val context = LocalContext.current
            state.bookmark?.let { bookmark ->
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // TODO Exercise 3: Make URL clickable to open in browser
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.url))
                        context.startActivity(intent)
                    },
                    )
                // TODO Exercise 14: Add "Copy URL" button, format created date

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bookmark.notes.ifEmpty { "No notes" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (bookmark.notes.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Exercise 16: Add TextField for editing notes
                // Stuck? Cmd+Click:
                Hints.Exercise16
                // Spacer(modifier = Modifier.height(16.dp))
                // var editedNotes by remember { mutableStateOf(bookmark.notes) }
                // OutlinedTextField(
                //     value = editedNotes,
                //     onValueChange = { editedNotes = it },
                //     label = { Text("Edit Notes") },
                //     modifier = Modifier.fillMaxWidth()
                // )
                // Spacer(modifier = Modifier.height(8.dp))
                // Button(onClick = { onAction(DetailAction.UpdateNotes(editedNotes)) }) {
                //     Text("Save")
                // }

            } ?: run {
                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailContentPreview() {
    DetailContent(
        state = DetailUiState(
            bookmark = Bookmark(
                id = "1",
                title = "Kotlin Documentation",
                url = "https://kotlinlang.org/docs/home.html",
                notes = "Great resource for learning Kotlin"
            )
        ),
        onAction = {},
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailContentEmptyNotesPreview() {
    DetailContent(
        state = DetailUiState(
            bookmark = Bookmark(
                id = "2",
                title = "Android Developers",
                url = "https://developer.android.com",
                notes = ""
            )
        ),
        onAction = {},
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())
    )
}
