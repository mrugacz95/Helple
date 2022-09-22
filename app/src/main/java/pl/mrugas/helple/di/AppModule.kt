package pl.mrugas.helple.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.mrugas.helple.data.WordDatabase

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideWordDatabase(
        @ApplicationContext applicationContext: Context
    ) = Room.databaseBuilder(applicationContext, WordDatabase::class.java, "words.db")
        .createFromAsset("database/words.db")
        .build()

    @Singleton
    @Provides
    fun provideWordDao(db: WordDatabase) = db.wordDao()
}
