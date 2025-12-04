package engineer.filip.hoarder

import engineer.filip.hoarder.data.model.Bookmark
import engineer.filip.hoarder.data.repository.BookmarkRepository

/**
 * Day 3 Exercise 4: Implement FakeBookmarkRepository for testing.
 *
 * Stuck? See Hints.Day3Exercise4Fake
 */
class FakeBookmarkRepository : BookmarkRepository {

    private val bookmarks = mutableListOf<Bookmark>()

    // TODO: Create in-memory storage
    var shouldThrowError = false

    override suspend fun getBookmarks(): List<Bookmark> {
        // TODO: Implement (check shouldThrowError)
        return bookmarks
    }

    override suspend fun getBookmarks(filter: String): List<Bookmark> {
        // TODO("Not yet implemented")
        return bookmarks.filter { bookmark ->
            bookmark.title.contains(filter) || bookmark.url.contains(
                filter
            )
        }
    }

    override suspend fun getBookmarkById(id: String): Bookmark? {
        // TODO: Implement
        return bookmarks.find { bookmark -> bookmark.id == id }
    }

    override suspend fun addBookmark(bookmark: Bookmark) {
        // TODO: Implement
        bookmarks.add(bookmark)
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        // TODO: Implement
        val index = bookmarks.indexOf(bookmark)
        bookmarks[index] = bookmark
    }

    override suspend fun deleteBookmark(bookmarkId: String) {
        // TODO: Implement
        bookmarks.removeAll { bookmark -> bookmark.id == bookmarkId }
    }

    override suspend fun clearAll() {
        // TODO: Implement
        bookmarks.clear()
    }

    fun getBookmarkCount(): Int {
        return bookmarks.size
    }

    override val next = 0
}
