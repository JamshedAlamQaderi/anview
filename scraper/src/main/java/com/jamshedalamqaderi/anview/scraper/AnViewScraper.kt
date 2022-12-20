package com.jamshedalamqaderi.anview.scraper

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.ext.QueryNodeExt.findNodes
import com.jamshedalamqaderi.anview.interfaces.Scraper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AnViewScraper private constructor() : AnViewBaseScraper() {
    companion object {
        fun create(
            nodeInfo: AccessibilityNodeInfo,
            queryJson: String
        ): Scraper<List<AccessibilityNodeInfo>> {
            return AnViewScraper().apply {
                query(queryJson)
                viewNode(nodeInfo)
            }
        }

        fun create(
            nodeInfo: AccessibilityNodeInfo,
            queryNode: QueryNode
        ): Scraper<List<AccessibilityNodeInfo>> {
            return AnViewScraper().apply {
                query(queryNode)
                viewNode(nodeInfo)
            }
        }

        fun create(): AnViewBaseScraper {
            return AnViewScraper()
        }
    }

    private lateinit var queryNode: QueryNode
    private lateinit var nodeInfo: AccessibilityNodeInfo

    override fun query(queryJson: String) {
        queryNode = Json.decodeFromString(queryJson)
    }

    override fun query(queryNode: QueryNode) {
        this.queryNode = queryNode
    }

    override fun viewNode(node: AccessibilityNodeInfo) {
        nodeInfo = node
    }

    override fun scrape(): List<AccessibilityNodeInfo> {
        return nodeInfo.findNodes(queryNode)
    }
}
