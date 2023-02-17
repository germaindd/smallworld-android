package com.example.smallworld.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class ProfileBottomSheetVisibility {
    HIDDEN,
    SHOWING
}

private val extraSpaceForBounce = 100.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileBottomSheet(
    bottomSheetVisibility: ProfileBottomSheetVisibility,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val swipeableState: SwipeableState<ProfileBottomSheetVisibility> = rememberSwipeableState(
        initialValue = bottomSheetVisibility
    )
    LaunchedEffect(bottomSheetVisibility) {
        swipeableState.animateTo(
            bottomSheetVisibility,
            spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
        )
    }
    AnimatedVisibility(
        visible = swipeableState.currentValue == ProfileBottomSheetVisibility.SHOWING,
        enter = slideInVertically(
            animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
        ) { it },
        exit = ExitTransition.None,
        modifier = modifier
    ) {
        val contentSize = remember { mutableStateOf(0f) }
        val anchors = remember(contentSize.value) {
            mapOf(
                0f to ProfileBottomSheetVisibility.SHOWING,
                contentSize.value to ProfileBottomSheetVisibility.HIDDEN
            )
        }
        Surface(
            modifier = Modifier
                .offset {
                    // extraSpaceForBounce compensating for the extra padding added below
                    IntOffset(
                        y = swipeableState.offset.value.roundToInt() + extraSpaceForBounce.roundToPx(),
                        x = 0
                    )
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Vertical
                )
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            ),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(extraSpaceForBounce)
                    .onGloballyPositioned { contentSize.value = it.size.height.toFloat() }
                    .padding(bottom = 8.dp)
            ) {
                DragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                content()
            }
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
    ) {
    }
}

@Preview
@Composable
fun PreviewDragHandle() {
    Column { DragHandle() }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewBottomSheet() {
    ProfileBottomSheet(content = {
        Box(Modifier.height(300.dp))
    }, onDismiss = {}, bottomSheetVisibility = ProfileBottomSheetVisibility.SHOWING)
}