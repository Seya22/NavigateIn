package com.example.navigator.presentation.preview

import com.example.navigator.domain.hit_test.HitTestResult
import com.example.navigator.domain.tree.TreeNode
import com.example.navigator.presentation.LabelObject
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.ArFrame

sealed interface MainEvent{
    class NewFrame(val frame: ArFrame): MainEvent
    class NewConfirmationObject(val confObject: LabelObject): MainEvent
    class TrySearch(val number: String, val changeType: Int): MainEvent
    class AcceptConfObject(val confirmType: Int): MainEvent
    class RejectConfObject(val confirmType: Int): MainEvent
    class NewSelectedNode(val node: TreeNode?): MainEvent
    object ChangeLinkMode: MainEvent
    class CreateNode(
        val number: String? = null,
        val position: Float3? = null,
        val orientation: Quaternion? = null,
        val hitTestResult: HitTestResult
    ): MainEvent
    class DeleteNode(val node: TreeNode): MainEvent
    class LinkNodes(val node1: TreeNode, val node2: TreeNode): MainEvent
    object LoadRecords: MainEvent
}
