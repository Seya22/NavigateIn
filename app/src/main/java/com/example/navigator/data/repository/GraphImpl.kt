package com.example.navigator.data.repository

import com.example.navigator.data.data_source.Database
import com.example.navigator.data.model.TreeNodeDto
import com.example.navigator.domain.repository.GraphRepository
import com.example.navigator.domain.tree.TreeNode
import com.example.navigator.domain.use_cases.convert
import com.example.navigator.domain.use_cases.opposite
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.toFloat3
import io.github.sceneview.math.toOldQuaternion
import io.github.sceneview.math.toVector3
import kotlinx.coroutines.*
import javax.inject.Inject

class GraphImpl @Inject constructor(
    private val database: Database
) : GraphRepository {

    private val dao = database.graphDao

    // Background coroutine to run the WAL checkpoint every 15 seconds
    private val walCheckpointJob = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            forceCheckpoint()  // Runs checkpoint every 15 seconds
            delay(15000) // 15 seconds
        }
    }

    override suspend fun getNodes(): List<TreeNodeDto> {
        return dao.getNodes() ?: listOf()
    }

    override suspend fun insertNodes(
        nodes: List<TreeNode>,
        translocation: Float3,
        rotation: Quaternion,
        pivotPosition: Float3
    ) {
        val transNodes = nodes.toMutableList()
        val undoTranslocation = translocation * -1f
        val undoQuaternion = rotation.opposite()

        dao.insertNodes(
            transNodes.map { node ->
                when (node) {
                    is TreeNode.Entry -> {
                        TreeNodeDto.fromTreeNode(
                            node = node,
                            position = undoPositionConvert(
                                node.position, undoTranslocation, undoQuaternion, pivotPosition
                            ),
                            forwardVector = node.forwardVector.convert(undoQuaternion)
                        )
                    }
                    is TreeNode.Path -> {
                        TreeNodeDto.fromTreeNode(
                            node = node,
                            position = undoPositionConvert(
                                node.position, undoTranslocation, undoQuaternion, pivotPosition
                            )
                        )
                    }
                }
            }
        )
    }

    override suspend fun deleteNodes(nodes: List<TreeNode>) {
        dao.deleteNodes(nodes.map { node -> TreeNodeDto.fromTreeNode(node) })
    }

    override suspend fun updateNodes(
        nodes: List<TreeNode>,
        translocation: Float3,
        rotation: Quaternion,
        pivotPosition: Float3
    ) {
        val transNodes = nodes.toMutableList()
        val undoTranslocation = translocation * -1f
        val undoQuarterion = rotation.opposite()

        dao.updateNodes(
            transNodes.map { node ->
                when (node) {
                    is TreeNode.Entry -> {
                        TreeNodeDto.fromTreeNode(
                            node = node,
                            position = undoPositionConvert(
                                node.position, undoTranslocation, undoQuarterion, pivotPosition
                            ),
                            forwardVector = node.forwardVector.convert(undoQuarterion)
                        )
                    }
                    is TreeNode.Path -> {
                        TreeNodeDto.fromTreeNode(
                            node = node,
                            position = undoPositionConvert(
                                node.position, undoTranslocation, undoQuarterion, pivotPosition
                            )
                        )
                    }
                }
            }
        )
    }

    override suspend fun clearNodes() {
        dao.clearNodes()
    }

    /**
     * 🚀 Runs WAL checkpoint every 15 seconds in a background thread.
     */
    private fun forceCheckpoint() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL);")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Call this function when you want to stop the background job (e.g., on app close).
     */
    fun stopCheckpointJob() {
        walCheckpointJob.cancel()
    }

    /**
     * Converts position with rotation and translation adjustments.
     */
    private fun undoPositionConvert(
        position: Float3,
        translocation: Float3,
        quaternion: Quaternion,
        pivotPosition: Float3
    ): Float3 {
        return (com.google.ar.sceneform.math.Quaternion.rotateVector(
            quaternion.toOldQuaternion(),
            (position - pivotPosition - translocation).toVector3()
        ).toFloat3() + pivotPosition)
    }
}
