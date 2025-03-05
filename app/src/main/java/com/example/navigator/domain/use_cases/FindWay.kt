package com.example.navigator.domain.use_cases

import com.example.navigator.domain.pathfinding.Path
import com.example.navigator.domain.pathfinding.Pathfinder
import com.example.navigator.domain.tree.Tree

class FindWay(
    private val pathfinder: Pathfinder
) {

    suspend operator fun invoke(
        from: String,
        to: String,
        tree: Tree
    ): Path? {
        return pathfinder.findWay(from, to, tree)
    }
}