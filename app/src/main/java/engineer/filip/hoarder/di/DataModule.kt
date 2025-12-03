package engineer.filip.hoarder.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import engineer.filip.hoarder.ui.Hints
import javax.inject.Singleton

/**
 * Hilt module for providing concrete data dependencies.
 *
 * Day 2 Exercise 10: Add a @Provides function for SharedPreferences.
 *
 * Stuck? See Hints.Day2Exercise10
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // TODO: Add @Provides @Singleton function for SharedPreferences
    @Provides
    @Singleton
    fun provideDataModule(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("hoarderBookmarks", Context.MODE_PRIVATE)
    }

    // Hint reference
    @Suppress("unused")
    private val _hint = Hints.Day2Exercise10
}
