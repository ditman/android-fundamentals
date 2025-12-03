package engineer.filip.hoarder.data.repository

import engineer.filip.hoarder.data.model.Bookmark

/**
 * Repository interface for bookmark operations.
 * ViewModel depends on this interface, not the implementation.
 * This allows easy swapping of data sources (in-memory → SharedPreferences → DB).
 */
interface BookmarkRepository {
    suspend fun getBookmarks(): List<Bookmark>
    suspend fun getBookmarkById(id: String): Bookmark?
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun updateBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmarkId: String)
    suspend fun clearAll()

    /**
     * The index of the next bookmark, which increases when `addBookmark` is called.
     */
    val next: Int
}
