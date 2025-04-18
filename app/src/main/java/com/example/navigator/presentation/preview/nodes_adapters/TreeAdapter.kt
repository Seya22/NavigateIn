package com.example.navigator.presentation.preview.nodes_adapters

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.navigator.domain.tree.TreeNode
import com.example.navigator.presentation.common.helpers.DrawerHelper
import com.uchuhimo.collections.MutableBiMap
import com.uchuhimo.collections.mutableBiMapOf
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArNode

class TreeAdapter(
    drawerHelper: DrawerHelper,
    previewView: ArSceneView,
    bufferSize: Int,
    scope: LifecycleCoroutineScope,
): NodesAdapter<TreeNode>(drawerHelper, previewView, bufferSize, scope) {

    private val modelsToLinkModels: MutableBiMap<Pair<ArNode, ArNode>, ArNode> = mutableBiMapOf()

    override suspend fun onInserted(item: TreeNode): ArNode {
        val node1 = drawerHelper.drawNode(item, previewView)
        for (id in item.neighbours) {
            nodes.keys.firstOrNull { it.id == id }?.let { treeNode ->
                nodes[treeNode]?.let { node2 ->
                    if (modelsToLinkModels[Pair(node1, node2)] == null ){
                        drawerHelper.drawLine(
                            node1,
                            node2,
                            modelsToLinkModels,
                            previewView
                        )
                    }
                }
            }
        }
        return node1
    }

    override suspend fun onRemoved(item: TreeNode, node: ArNode) {
        drawerHelper.removeNode(node)
        modelsToLinkModels.keys
            .filter { it.first == node || it.second == node }
            .forEach { pair ->
                drawerHelper.removeLink(pair, modelsToLinkModels)
            }
    }

    suspend fun createLink(treeNode1: TreeNode, treeNode2: TreeNode) {
        val node1 = nodes[treeNode1]
        val node2 = nodes[treeNode2]
        if (node1 != null && node2 != null) {
            drawerHelper.drawLine(
                node1,
                node2,
                modelsToLinkModels,
                previewView
            )
        }
    }

    fun getArNode(treeNode: TreeNode?): ArNode? = nodes[treeNode]

    fun getTreeNode(node: ArNode?): TreeNode? {
        node?.let {
            return nodes.entries.find { it.value == node }?.key
        }
        return null
    }
}