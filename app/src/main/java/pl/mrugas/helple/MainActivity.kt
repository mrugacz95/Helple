package pl.mrugas.helple

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.mrugas.helple.data.WordDatabase
import pl.mrugas.helple.ui.GameView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameView() }
        checkDb()
    }

    fun checkDb(){
        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(applicationContext, WordDatabase::class.java, "words.db")
                .createFromAsset("database/words.db")
                .build()
            val words = db.wordDao().getSomeWords()
            val count = db.wordDao().count()
            Log.d("words", "$words $count")
        }
    }


}

