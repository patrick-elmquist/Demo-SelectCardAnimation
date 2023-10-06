package com.patrick.elmquist.demo.selectcardanimation.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal data class CardModel(
    val title: String,
    val backgroundColor: Color,
)

@Composable
internal fun getCards(count: Int): List<CardModel> {
    return List(count) { i ->
        CardModel(
            title = "Card #${i + 1}",
            backgroundColor = colors[i]
        )
    }
}

val colors = listOf(
    Color(0x26, 0x46, 0x53),
    Color(0x2a, 0x9d, 0x8f),
    Color(0xe9, 0xc4, 0x6a),
    Color(0xf4, 0xa2, 0x61),
    Color(0xe7, 0x6f, 0x51),
)
