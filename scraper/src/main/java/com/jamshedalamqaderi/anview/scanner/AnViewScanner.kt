package com.jamshedalamqaderi.anview.scanner

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.ext.QueryNodeExt.traverse
import com.jamshedalamqaderi.anview.interfaces.Scanner
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AnViewScanner private constructor() : Scanner<List<AccessibilityNodeInfo>> {
    companion object {
        fun create(
            nodeInfo: AccessibilityNodeInfo,
            queryJson: String
        ): Scanner<List<AccessibilityNodeInfo>> {
            return AnViewScanner().apply {
                query(queryJson)
                nodeInfo(nodeInfo)
            }
        }

        fun create(
            nodeInfo: AccessibilityNodeInfo,
            queryNode: QueryNode
        ): Scanner<List<AccessibilityNodeInfo>> {
            return AnViewScanner().apply {
                query(queryNode)
                nodeInfo(nodeInfo)
            }
        }
    }

    private lateinit var queryNode: QueryNode
    private lateinit var nodeInfo: AccessibilityNodeInfo

    private fun query(queryJson: String) {
        queryNode = Json.decodeFromString(queryJson)
    }

    private fun query(queryNode: QueryNode) {
        this.queryNode = queryNode
    }

    fun nodeInfo(nodeInfo: AccessibilityNodeInfo) {
        this.nodeInfo = nodeInfo
    }

    override fun scan(): List<AccessibilityNodeInfo> {
        return queryNode.traverse(nodeInfo)
    }
}

