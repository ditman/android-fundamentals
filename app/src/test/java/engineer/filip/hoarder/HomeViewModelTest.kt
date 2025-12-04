package engineer.filip.hoarder

import engineer.filip.hoarder.data.ShareHandler
import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.ui.home.HomeAction
import engineer.filip.hoarder.ui.home.HomeViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Day 3 Exercise 4: Write HomeViewModel tests.
 *
 * Stuck? See Hints.Day3Exercise4Tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBookmarkRepository
    private lateinit var viewModel: HomeViewModel

    private lateinit var shareHandler: ShareHandler

    @Before
    fun setup() {
        repository = FakeBookmarkRepository()
        shareHandler = ShareHandler()
        viewModel = HomeViewModel(repository, shareHandler)
    }

    @Test
    fun `initial state has empty bookmarks`() = runTest {
        // TODO: Implement
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state.bookmarks.isEmpty())
    }

    @Test
    fun `adding bookmark updates state`() = runTest {
        // TODO: Implement
        // viewModel.onAction(HomeAction.LoadBookmarks)
        val bookmark = Bookmark(
            id = "1",
            title = "Title",
            url = "https://flutter.dev",
        )
        viewModel.onAction(HomeAction.AddBookmark(bookmark))
        val state = viewModel.uiState.value
        val bookmarks = state.bookmarks
        assertEquals(1, bookmarks.size)
    }

    @Test
    fun `deleting bookmark removes from state`() = runTest {
        // TODO: Implement
    }

    @Test
    fun `loading bookmarks shows bookmarks`() = runTest {
        // TODO: Implement
    }

    @Test
    fun `error state is set when repository throws`() = runTest {
        // TODO: Implement
    }
}
