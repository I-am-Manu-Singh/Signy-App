package com.myapplication.common.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun RTSPPlayer(url: String, modifier: Modifier)
