package pl.mrugas.helple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.mrugas.helple.ui.MainActivityView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainActivityView() }
        gameViewModel.init()
    }
}

