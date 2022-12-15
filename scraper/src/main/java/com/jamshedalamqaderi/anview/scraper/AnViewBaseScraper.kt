package com.jamshedalamqaderi.anview.scraper

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.interfaces.Scraper

abstract class AnViewBaseScraper : Scraper<List<AccessibilityNodeInfo>> {
    abstract fun query(queryJson: String)
    abstract fun query(queryNode: QueryNode)
    abstract fun viewNode(node: AccessibilityNodeInfo)
}