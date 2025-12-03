package engineer.filip.hoarder.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareHandler @Inject constructor() {
    private val _pendingShare = MutableStateFlow<String?>(value = null)
    val pendingShare: StateFlow<String?> = _pendingShare.asStateFlow()

    private val _deeplinkData = MutableStateFlow<String?>(value = null)
    val deeplinkData: StateFlow<String?> = _deeplinkData.asStateFlow()

    fun onShareReceived(text: String?) {
        Log.d("ShareHandler", "onShareReceived: $text")
        text?.let { input ->
            _pendingShare.update { input }
        }
    }

    fun consumeIntent() {
        _pendingShare.update { null }
    }

    fun onDeeplinkReceived(bookmarkId: String?) {
        bookmarkId?.let { id -> _deeplinkData.update { id } }
    }

    fun consumeDeepLink() {
        _deeplinkData.update { null }
    }
}
