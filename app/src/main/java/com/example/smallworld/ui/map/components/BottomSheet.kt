@file:OptIn(ExperimentalMaterialApi::class)

package com.example.smallworld.ui.map.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class BottomSheetVisibility {
    HIDDEN, SHOWING
}

@OptIn(ExperimentalMaterialApi::class)
@Stable
class BottomSheetState(
    val swipeableState: SwipeableState<BottomSheetVisibility>
) {
    val currentVisibility get() = swipeableState.currentValue

    private suspend fun animateTo(visibility: BottomSheetVisibility) {
        swipeableState.animateTo(
            visibility, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
        )
    }

    suspend fun show() = animateTo(BottomSheetVisibility.SHOWING)

    suspend fun hide() = animateTo(BottomSheetVisibility.HIDDEN)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberBottomSheetState(initialValue: BottomSheetVisibility): BottomSheetState {
    val swipeableState = rememberSwipeableState(initialValue = initialValue)
    return remember(swipeableState) {
        BottomSheetState(swipeableState)
    }
}

private val extraSpaceForBounce = 100.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    bottomSheetState: BottomSheetState,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit),
) {

    val contentSize = remember { mutableStateOf(200f) }
    val anchors = remember(contentSize.value) {
        mapOf(
            0f to BottomSheetVisibility.SHOWING,
            contentSize.value to BottomSheetVisibility.HIDDEN,
        )
    }
    Surface(
        modifier = modifier
            .offset {
                // extraSpaceForBounce compensating for the extra padding added below
                IntOffset(
                    y = bottomSheetState.swipeableState.offset.value.roundToInt() + extraSpaceForBounce.roundToPx(),
                    x = 0
                )
            }
            .swipeable(
                state = bottomSheetState.swipeableState,
                anchors = anchors,
                orientation = Orientation.Vertical
            )
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
        ),
        shadowElevation = 5.dp
    ) {
        Column(Modifier
            .fillMaxWidth()
            .padding(bottom = extraSpaceForBounce)
            .onGloballyPositioned { contentSize.value = it.size.height.toFloat() }
            .padding(bottom = 8.dp))
        {
            DragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            content()
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .padding(top = 22.dp)
            .size(width = 32.dp, height = 4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.extraLarge
    ) {}
}

@Preview
@Composable
fun PreviewDragHandle() {
    Column { DragHandle() }
}

@Preview
@Composable
fun PreviewBottomSheet() {
    Box(
        modifier = Modifier
            .background(Color.Blue)
            .width(420.dp)
            .height(500.dp)
    ) {
        BottomSheet(
            bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetVisibility.SHOWING)
        ) {
            Box(Modifier.height(300.dp))
        }
    }
}