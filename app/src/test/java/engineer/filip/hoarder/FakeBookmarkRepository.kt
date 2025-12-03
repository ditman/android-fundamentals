package engineer.filip.hoarder

import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.data.repository.BookmarkRepository

/**
 * Day 3 Exercise 4: Implement FakeBookmarkRepository for testing.
 *
 * Stuck? See Hints.Day3Exercise4Fake
 */
class FakeBookmarkRepository : BookmarkRepository {

    // TODO: Create in-memory storage
    var shouldThrowError = false

    override suspend fun getBookmarks(): List<Bookmark> {
        // TODO: Implement (check shouldThrowError)
        return emptyList()
    }

    override suspend fun getBookmarkById(id: String): Bookmark? {
        // TODO: Implement
        return null
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        // TODO: Implement
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        // TODO: Implement
    }

    override suspend fun deleteBookmark(bookmarkId: String) {
        // TODO: Implement
    }

    override suspend fun clearAll() {
        // TODO: Implement
    }

    fun getBookmarkCount(): Int = 0 // TODO: return bookmarks.size

    override val next = 0
}
