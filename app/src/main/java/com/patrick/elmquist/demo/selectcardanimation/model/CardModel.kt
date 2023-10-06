package com.patrick.elmquist.demo.selectcardanimation.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal data class CardModel(
    val title: String,
    val backgroundColor: Color,
    val url: String,
)

@Composable
internal fun getCards(count: Int): List<CardModel> {
    return List(count) { i ->
        CardModel(
            title = urls[i].let { url ->
                val index = url.lastIndexOf('/')
                url.substring(index + 1).drop(4).removeSuffix(".png")
            },
            backgroundColor = colors[i],
            url = urls[i],
        )
    }.reversed()
}

val colors = listOf(
    Color(0x26, 0x46, 0x53),
    Color(0x2a, 0x9d, 0x8f),
    Color(0xe9, 0xc4, 0x6a),
    Color(0xf4, 0xa2, 0x61),
    Color(0xe7, 0x6f, 0x51),
)

val urls = listOf(
    "https://archives.bulbagarden.net/media/upload/f/fb/0001Bulbasaur.png",
    "https://archives.bulbagarden.net/media/upload/1/1c/0149Dragonite.png",
    "https://archives.bulbagarden.net/media/upload/4/4a/0025Pikachu.png",
    "https://archives.bulbagarden.net/media/upload/2/27/0004Charmander.png",
    "https://archives.bulbagarden.net/media/upload/3/38/0006Charizard.png"
)
