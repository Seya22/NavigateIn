package com.example.navigator.presentation.preview.state

import com.example.navigator.domain.pathfinding.Path
import com.example.navigator.domain.tree.TreeNode

data class PathState(
    val startEntry: TreeNode.Entry? = null,
    val endEntry: TreeNode.Entry? = null,
    val path: Path? = null
)
