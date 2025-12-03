package engineer.filip.hoarder.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.data.model.toBookmarkList
import engineer.filip.hoarder.ui.Hints
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Day 2 Exercise 10: Add persistence using SharedPreferences + Gson.
 *
 * Stuck? See Hints.Day2Exercise10
 */
@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    // TODO: Inject SharedPreferences here
    private val preferences: SharedPreferences
) : BookmarkRepository {
    private val BOOKMARKS_STORAGE_KEY = "bookmarks_key"

    override var next: Int = 0
        private set

    // TODO: Create Gson instance
    private val gson = Gson()

    private val bookmarks = mutableListOf<Bookmark>()

    // TODO: Implement loadFromPrefs()
    // TODO: Implement saveToPrefs()
    private fun saveToPrefs() {
        preferences.edit {
            putString(BOOKMARKS_STORAGE_KEY, gson.toJson(bookmarks)).apply()
        }
    }

    private fun loadFromPrefs(): List<Bookmark> {
        val json = preferences.getString(BOOKMARKS_STORAGE_KEY, null)
        val prefsBookmarks = json.toBookmarkList()
        bookmarks.clear()
        bookmarks.addAll(prefsBookmarks)
        return bookmarks.toList()
    }

    override suspend fun getBookmarks(): List<Bookmark> {
        return loadFromPrefs()
    }

    override suspend fun getBookmarks(filter: String): List<Bookmark> {
        return loadFromPrefs().filter { bookmark ->
            bookmark.title.contains(filter) || bookmark.url.contains(
                filter
            )
        }
    }

    override suspend fun getBookmarkById(id: String): Bookmark? {
        return bookmarks.find { it.id == id }
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        bookmarks.add(bookmark)
        next++;
        // TODO: Call saveToPrefs()
        saveToPrefs()
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        val index = bookmarks.indexOfFirst { it.id == bookmark.id }
        if (index != -1) {
            bookmarks[index] = bookmark
            // TODO: Call saveToPrefs()
            saveToPrefs()
        }
    }

    override suspend fun deleteBookmark(bookmarkId: String) {
        bookmarks.removeAll { it.id == bookmarkId }
        // TODO: Call saveToPrefs()
        saveToPrefs()
    }

    override suspend fun clearAll() {
        bookmarks.clear()
        // TODO: Call saveToPrefs()
        saveToPrefs()
    }

    // TODO: create companion object with private shared prefs key

    // Hint reference
    @Suppress("unused")
    private val _hint = Hints.Day2Exercise10
}
