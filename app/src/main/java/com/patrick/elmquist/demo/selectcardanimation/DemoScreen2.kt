package com.patrick.elmquist.demo.selectcardanimation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrick.elmquist.demo.selectcardanimation.model.CardModel
import com.patrick.elmquist.demo.selectcardanimation.model.getCards
import com.patrick.elmquist.demo.selectcardanimation.swipe.Twyper
import com.patrick.elmquist.demo.selectcardanimation.swipe.rememberTwyperController
import com.patrick.elmquist.demo.selectcardanimation.ui.theme.DemoSelectCardAnimationTheme

@Composable
internal fun DemoScreen2(items: List<CardModel>) {
    val items = remember { items.toMutableList() }
    val twyperController = rememberTwyperController()

    Twyper(
        items = items,
        twyperController = twyperController,
        stackCount = 5,
        onItemRemoved = { item, direction ->
            println("Item removed: $item -> $direction")
            items.remove(item)
        },
        onEmpty = {
            println("End reached")
        }
    ) { item ->
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(item.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item.title, fontSize = 20.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDemoScreen2() {
    DemoSelectCardAnimationTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DemoScreen2(getCards(5))
        }
    }
}
