package engineer.filip.hoarder.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import engineer.filip.hoarder.data.model.Bookmark
import java.util.UUID

/**
 * Screen-level composable. Handles ViewModel and state management.
 * This is what navigation calls.
 *
 * Events (one-time navigation) are observed via SharedFlow.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToDeleteConfirmation: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe one-time events for navigation
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToDetail -> onNavigateToDetail(event.bookmarkId)
                is HomeEvent.NavigateToDeleteConfirmation -> onNavigateToDeleteConfirmation(event.bookmarkId)
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onAction(HomeAction.LoadBookmarks)
        }
    }

    HomeContent(
        state = uiState, onAction = viewModel::onAction
    )
}

/**
 * Pure UI composable. See exercise hints for implementation details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState, onAction: (HomeAction) -> Unit, modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = { Text("Hoarder") },
            // TODO Exercise 11: Add ClearAll IconButton. See Hints.Exercise11
            actions = {
                IconButton(
                    onClick = {
                        onAction(
                            HomeAction.ClearAll
                        )
                    }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                    )
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onAction(
                    HomeAction.AddBookmark(
                        Bookmark(
                            id = UUID.randomUUID().toString(),
                            title = "My random bookmark",
                            url = "https://www.flutter.dev",
                        )
                    )
                )
            }) {
            Text(text = "#${state.counter}", fontSize = 16.sp)
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchInput(modifier = modifier, searchQuery = state.searchQuery, onAction = onAction)
            // TODO Day 2 Exercise 11: Add loading state. See Hints.Day2Exercise11
            // TODO Day 2 Exercise 12: Add error state with retry. See Hints.Day2Exercise12
            // TODO Day 1 Exercise 9: Add empty state. See Hints.Exercise9
            // TODO Day 2 Exercise 13: Add search TextField. See Hints.Day2Exercise13
            when {
                state.bookmarks.isEmpty() -> {
                    EmptyState(modifier = Modifier.padding(innerPadding))
                }

                else -> {
                    BookmarkItems(
                        modifier = Modifier.padding(innerPadding),
                        bookmarks = state.bookmarks,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
fun SearchInput(
    modifier: Modifier = Modifier, searchQuery: String, onAction: (HomeAction) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        label = { Text("Search") },
        onValueChange = { newValue ->
            onAction(HomeAction.SearchQueryChanged(newValue))
        })
}

@Composable
fun BookmarkItems(
    modifier: Modifier = Modifier, bookmarks: List<Bookmark>, onAction: (HomeAction) -> Unit
) {
    // Day 3 Exercise 4b: Find the performance issue in this LazyColumn
    // Hint: Look at how lambdas are passed to BookmarkItem
    // Stuck? See Hints.Day3Exercise4b
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(items = bookmarks) { bookmark ->
            BookmarkItem(
                bookmark = bookmark,
                onClick = { onAction(HomeAction.BookmarkClick(bookmark.id)) },
                onDeleteClick = { onAction(HomeAction.DeleteBookmarkClick(bookmark.id)) })
        }
    }
}

/**
 * Day 1 Exercise 8: Add border to Card. See Hints.Exercise8
 */
@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val starShape = remember {
        RoundedPolygonShape(
            RoundedPolygon.star(
                numVerticesPerRadius = 16,
                rounding = CornerRounding(0.33f),
            )
        )
    }
    // TODO Exercise 8: Add .border(2.dp, Color.Blue) to Card modifier
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp,
        ),
        shape = starShape,
        border = BorderStroke(width = 2.dp, color = Color(0xFFFABADA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = bookmark.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (bookmark.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bookmark.notes, style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete bookmark"
                )
            }
        }
    }
}

/**
 * Day 1 Exercise 9: Build EmptyState composable. See Hints.Exercise9
 */
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    // TODO: Implement empty state UI (icon, title, subtitle)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val message = "Nothing to see here"
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CollectionsBookmark,
                contentDescription = "None",
                tint = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                message,
                fontSize = 36.sp,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    EmptyState()
}

@Preview(showBackground = true)
@Composable
private fun BookmarkItemPreview() {
    BookmarkItem(
        bookmark = Bookmark(
        id = "1",
        title = "Kotlin Docs",
        url = "https://kotlinlang.org",
        notes = "Great documentation"
    ), onClick = {}, onDeleteClick = {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        state = HomeUiState(
            bookmarks = listOf(
                Bookmark("1", "Google", "https://google.com", "Search engine"),
                Bookmark("2", "Kotlin", "https://kotlinlang.org", ""),
                Bookmark("3", "Android", "https://developer.android.com", "Dev docs")
            )
        ), onAction = {}, modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentEmptyPreview() {
    HomeContent(
        state = HomeUiState(bookmarks = emptyList()),
        onAction = {},
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())
    )
}
