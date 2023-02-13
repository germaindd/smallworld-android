package com.example.smallworld.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.smallworld.databinding.LayoutFragmentContainerBinding

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    AndroidViewBinding(
        LayoutFragmentContainerBinding::inflate,
        modifier = modifier
    )
}