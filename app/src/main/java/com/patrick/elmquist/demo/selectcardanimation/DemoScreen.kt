@file:OptIn(ExperimentalMaterial3Api::class)

package com.patrick.elmquist.demo.selectcardanimation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.patrick.elmquist.demo.selectcardanimation.model.CardModel
import com.patrick.elmquist.demo.selectcardanimation.model.getCards
import com.patrick.elmquist.demo.selectcardanimation.ui.theme.DemoSelectCardAnimationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun DemoScreen(cards: List<CardModel>) {
    var selectedIndex by remember { mutableStateOf(SelectedIndex.NotInitiated) }

    val screenHeight = getScreenHeight()
    val density = LocalDensity.current
    val cardsAndOffsets = remember(cards) {
        cards.associateWith { Animatable(initialValue = screenHeight) }
    }

    LaunchedEffect(selectedIndex) {
        when (selectedIndex) {
            SelectedIndex.NotInitiated -> {
                delay(EnterAnimationDelay)
                animateToDefaultPosition(
                    offsets = cardsAndOffsets.values,
                    itemSpacing = with(density) { LargeStackSpacing.roundToPx() },
                    itemDelay = EnterCardAnimationDelay,
                    animationSpec = animationSpecIn,
                )
            }

            SelectedIndex.NotSelected -> {
                animateToDefaultPosition(
                    offsets = cardsAndOffsets.values,
                    itemSpacing = with(density) { LargeStackSpacing.roundToPx() },
                    animationSpec = animationSpecRearrange,
                )
            }

            else -> {
                animateSelectingCard(
                    items = cardsAndOffsets.values,
                    selectedIndex = selectedIndex,
                    itemSpacing = with(density) { CompressedStackSpacing.roundToPx() },
                    stackOffset = with(density) { CompressedStackOffset.roundToPx() },
                )
            }
        }
    }

    var cardHeight by remember { mutableIntStateOf(0) }
    var boxHeight by remember { mutableIntStateOf(0) }
    val topSpacing = with(LocalDensity.current) {
        if (boxHeight == 0) {
            0.dp
        } else {
            (boxHeight - cardHeight - LargeStackSpacing.roundToPx() * (cards.size - 1)).toDp() / 2
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { boxHeight = it.height }
        .padding(top = topSpacing)) {
        cardsAndOffsets.entries.forEachIndexed { i, (model, offset) ->
            Card(
                model = model,
                showDetails = selectedIndex.index != i,
                onClick = {
                    selectedIndex = if (selectedIndex.index == i) {
                        SelectedIndex.NotSelected
                    } else {
                        SelectedIndex(i)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .onSizeChanged { cardHeight = it.height }
                    .offset { IntOffset(x = 0, offset.value.roundToInt()) },
            )
        }
    }
}

private fun CoroutineScope.animateSelectingCard(
    items: Collection<Animatable<Float, AnimationVector1D>>,
    selectedIndex: SelectedIndex,
    stackOffset: Int,
    itemSpacing: Int,
) {
    val compressedStackOffsets = items
        .filterIndexed { index, _ -> index != selectedIndex.index }
        .mapIndexed { index, item -> item to (stackOffset + index * itemSpacing).toFloat() }
        .toMap()

    items.forEachIndexed { i, item ->
        launch {
            val targetValue = if (i == selectedIndex.index) {
                0f
            } else {
                compressedStackOffsets.getValue(item)
            }
            item.animateTo(
                targetValue = targetValue,
                animationSpec = animationSpecRearrange,
            )
        }
    }
}

private fun CoroutineScope.animateToDefaultPosition(
    offsets: Collection<Animatable<Float, AnimationVector1D>>,
    itemSpacing: Int,
    animationSpec: AnimationSpec<Float>,
    itemDelay: Long = 0L,
) {
    offsets.forEachIndexed { index, offset ->
        launch {
            delay(timeMillis = index * itemDelay)
            offset.animateTo(
                targetValue = (index * itemSpacing).toFloat(),
                animationSpec = animationSpec,
            )
        }
    }
}

@Composable
private fun Card(
    model: CardModel,
    showDetails: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = model.backgroundColor),
        elevation = CardDefaults.elevatedCardElevation(24.dp),
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp).height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val smallImage = Modifier.padding(end = 12.dp).size(24.dp)
                AnimatedVisibility(showDetails) {
                    if (LocalInspectionMode.current) {
                        Box(modifier = smallImage.background(color = Color.LightGray))
                    } else {
                        AsyncImage(
                            model = model.url,
                            modifier = smallImage,
                            contentDescription = null,
                        )
                    }
                }

                Text(
                    text = model.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.54f),
                )
            }
            val modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .aspectRatio(1f)
                .fillMaxWidth()
            if (LocalInspectionMode.current) {
                Box(modifier = modifier.background(color = Color.LightGray))
            } else {
                AsyncImage(
                    model = model.url,
                    modifier = modifier,
                    contentDescription = null,
                )
            }
        }
    }
}

@JvmInline
private value class SelectedIndex(val index: Int) {
    companion object {
        val NotSelected = SelectedIndex(-1)
        val NotInitiated = SelectedIndex(-2)
    }
}

private val LargeStackSpacing = 50.dp
private val CompressedStackOffset = 375.dp
private val CompressedStackSpacing = 48.dp
private const val EnterAnimationDelay = 500L
private const val EnterCardAnimationDelay = 50L

private val animationSpecIn = tween<Float>(durationMillis = 400, easing = FastOutSlowInEasing)
private val animationSpecRearrange =
    tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)

@Composable
private fun getScreenHeight(): Float =
    with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

@Preview(showBackground = true)
@Composable
fun PreviewDemoScreen() {
    DemoSelectCardAnimationTheme {
        DemoScreen(getCards(5))
    }
}
