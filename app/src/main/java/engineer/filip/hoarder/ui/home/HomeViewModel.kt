package engineer.filip.hoarder.ui.home

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import engineer.filip.hoarder.data.ShareHandler
import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.data.repository.BookmarkRepository
import engineer.filip.hoarder.ui.Hints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Immutable UI state for the Home screen.
 * All UI state lives here - single source of truth.
 *
 * Day 1 Exercise 10: Add an `itemCount: Int = 0` field
 * Day 2 Exercise 13: Add searchQuery for debounce search
 *
 * Stuck? Cmd+Click: [Hints.Exercise10] or [Hints.Day2Exercise13]
 */
@Immutable
data class HomeUiState(
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val counter: Int = 0,
    val searchQuery: String? = null,

    // TODO Day 2 Exercise 13: Add val searchQuery: String = ""
) {
    @Suppress("unused")
    private val _hint10 = Hints.Exercise10

    @Suppress("unused")
    private val _hint13 = Hints.Day2Exercise13
}

/**
 * User actions/intents for the Home screen.
 * Sealed interface ensures exhaustive handling in `when` expressions.
 *
 * Day 1 Exercise 11: Add `data object ClearAll : HomeAction`
 * Day 2 Exercise 13: Add `data class SearchQueryChanged(val query: String) : HomeAction`
 *
 * Stuck? Cmd+Click: [Hints.Exercise11] or [Hints.Day2Exercise13]
 */
@Stable
sealed interface HomeAction {
    data object LoadBookmarks : HomeAction
    data class AddBookmark(val bookmark: Bookmark) : HomeAction
    data class DeleteBookmark(val bookmarkId: String) : HomeAction
    data class DeleteBookmarkClick(val bookmarkId: String) : HomeAction
    data class BookmarkClick(val bookmarkId: String) : HomeAction

    // TODO Day 1 Exercise 11: Add data object ClearAll : HomeAction
    data object ClearAll : HomeAction

    // TODO Day 2 Exercise 13: Add data class SearchQueryChanged(val query: String) : HomeAction
    data class SearchQueryChanged(val query: String) : HomeAction

    @Suppress("unused")
    private val _hint11: Any get() = Hints.Exercise11

    @Suppress("unused")
    private val _hint13: Any get() = Hints.Day2Exercise13
}

/**
 * One-time navigation events emitted via SharedFlow.
 * These are consumed once by the UI and trigger navigation.
 */
@Stable
sealed interface HomeEvent {
    data class NavigateToDetail(val bookmarkId: String) : HomeEvent
    data class NavigateToDeleteConfirmation(val bookmarkId: String) : HomeEvent
}

/**
 * Day 2 Exercise 8: Add ShareHandler injection and observe shared content.
 * Day 2 Exercise 13: Add search with debounce.
 *
 * Stuck? See Hints.Day2Exercise8 and Hints.Day2Exercise13
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BookmarkRepository,
    // TODO: Inject ShareHandler
    private val shareHandler: ShareHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    // TODO Exercise 13: Add search query flow for debounce

    init {
        loadBookmarks()
        // TODO Exercise 8: Call observeSharedContent()
        observeSharedContent()

        // TODO Exercise 13: Call observeSearch()
    }

    // TODO Exercise 8: Implement observeSharedContent()
    fun observeSharedContent() {
        Log.d("HomeViewModel", "observeSharedContent")
        viewModelScope.launch {
            shareHandler.pendingShare.filterNotNull().collect { bookmarkUrl ->
                Log.d("HomeViewModel", "Got bookmark $bookmarkUrl")
                // Create new bookmark
                val bookmark = Bookmark(
                    id = UUID.randomUUID().toString(),
                    url = bookmarkUrl,
                    title = bookmarkUrl,
                )
                addBookmark(bookmark)
                shareHandler.consumeIntent()
            }
        }
        viewModelScope.launch {
            shareHandler.deeplinkData.filterNotNull().collect { bookmarkId ->
                Log.d("HomeViewModel", "Got deeplink $bookmarkId")
                onBookmarkClick(bookmarkId)
                shareHandler.consumeDeepLink()
            }
        }
    }

    // TODO Exercise 9: Implement extractTitle(text: String)
    // TODO Exercise 13: Implement observeSearch()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadBookmarks -> loadBookmarks()
            is HomeAction.AddBookmark -> addBookmark(action.bookmark)
            is HomeAction.DeleteBookmark -> deleteBookmark(action.bookmarkId)
            is HomeAction.DeleteBookmarkClick -> onDeleteBookmarkClick(action.bookmarkId)
            is HomeAction.BookmarkClick -> onBookmarkClick(action.bookmarkId)
            is HomeAction.SearchQueryChanged -> onSearchQueryChanged(action.query)
            // TODO Exercise 11: Handle ClearAll
            is HomeAction.ClearAll -> clearAllBookmarks()
            // TODO Exercise 13: Handle SearchQueryChanged
        }
    }

    private fun onSearchQueryChanged(query: String) {
        Log.d("HomeViewModel", "onSearchQueryChanged: $query")
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    /**
     * Clears all the bookmarks
     */
    private fun clearAllBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
            loadBookmarks()
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bookmarks = repository.getBookmarks()
                // TODO Exercise 13: Cache bookmarks in allBookmarks for search filtering
                _uiState.update {
                    it.copy(
                        bookmarks = bookmarks,
                        isLoading = false,
                        error = null,
                        counter = bookmarks.size,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = uiState.value.bookmarks.size
            repository.addBookmark(bookmark.copy(title = bookmark.title + " ${repository.next}"))
            loadBookmarks()
        }
    }

    private fun deleteBookmark(bookmarkId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBookmark(bookmarkId)
            loadBookmarks()
        }
    }

    private fun onBookmarkClick(bookmarkId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.emit(HomeEvent.NavigateToDetail(bookmarkId))
        }
    }

    private fun onDeleteBookmarkClick(bookmarkId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.emit(HomeEvent.NavigateToDeleteConfirmation(bookmarkId))
        }
    }

    // TODO Exercise 11: Implement clearAll()

    @Suppress("unused")
    private val _hint8 = Hints.Day2Exercise8

    @Suppress("unused")
    private val _hint9 = Hints.Day2Exercise9

    @Suppress("unused")
    private val _hint11 = Hints.Exercise11

    @Suppress("unused")
    private val _hint13 = Hints.Day2Exercise13
}
