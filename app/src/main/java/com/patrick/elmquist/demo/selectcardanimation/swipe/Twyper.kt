package com.patrick.elmquist.demo.selectcardanimation.swipe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange

enum class SwipedOutDirection {
    LEFT, RIGHT
}

@Composable
inline fun <reified T> Twyper(
    items: List<T>,
    onItemRemoved: (T, SwipedOutDirection) -> Unit,
    onEmpty: () -> Unit = {},
    twyperController: TwyperController = rememberTwyperController(),
    stackCount: Int = 2,
    paddingBetweenCards: Float = 40f,
    modifier: Modifier = Modifier,
    crossinline renderItem: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        val list = items.take(stackCount).reversed()
        list.forEachIndexed { index, item ->
            key(item) {
                val cardController = rememberCardController()
                if (index == list.lastIndex) {
                    twyperController.currentCardController = cardController
                }
                if (!cardController.isCardOut()) {
                    val paddingTop by animateFloatAsState(targetValue = (index * paddingBetweenCards))
                    Card(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        cardController.onDragEnd()
                                    },
                                    onDragCancel = {
                                        cardController.onDragCancel()
                                    },
                                    onDrag = { change, dragAmount ->
                                        if (change.positionChange() != Offset.Zero) change.consume()
                                        cardController.onDrag(dragAmount)
                                    }
                                )
                            }
                            .graphicsLayer(
                                translationX = cardController.cardX,
                                translationY = cardController.cardY + paddingTop,
                                rotationZ = cardController.rotation,
                            )
                    ) {
                        renderItem(item)
                    }
                } else {
                    cardController.swipedOutDirection?.let { outDirection ->
                        onItemRemoved(item, outDirection)
                        if (items.isEmpty()) {
                            onEmpty()
                        }
                    }
                }
            }
        }
    }
}
