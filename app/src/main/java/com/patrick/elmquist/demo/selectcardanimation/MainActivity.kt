package com.patrick.elmquist.demo.selectcardanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.patrick.elmquist.demo.selectcardanimation.model.getCards
import com.patrick.elmquist.demo.selectcardanimation.ui.theme.DemoSelectCardAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoSelectCardAnimationTheme() {
                Surface {
                    DemoScreen(getCards(5))
                }
            }
        }
    }
}
