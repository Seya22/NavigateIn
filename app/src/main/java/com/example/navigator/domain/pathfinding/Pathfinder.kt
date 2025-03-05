package com.example.navigator.domain.pathfinding

import com.example.navigator.domain.tree.Tree

interface Pathfinder {

    suspend fun findWay(
        from: String,
        to: String,
        tree: Tree
    ): Path?

}