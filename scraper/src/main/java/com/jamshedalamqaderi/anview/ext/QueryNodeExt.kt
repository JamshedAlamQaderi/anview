package com.jamshedalamqaderi.anview.ext

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object QueryNodeExt {

    fun AccessibilityNodeInfo.findNodes(query: QueryNode): List<AccessibilityNodeInfo> {
        val nodeList = arrayListOf<AccessibilityNodeInfo>()
        findNodeList(query, nodeList)
        return nodeList
    }

    private fun AccessibilityNodeInfo.findNodeList(
        query: QueryNode,
        resultNodes: ArrayList<AccessibilityNodeInfo>,
        currentNodeIndex: Int? = null
    ) {
        val node = query.traverseTopToBottom(this, currentNodeIndex)
        if (node != null) {
            resultNodes.add(node);
            return
        }
        forEachChild { childNodeIndex, childNode ->
            childNode?.findNodeList(query, resultNodes, childNodeIndex)
        }
    }

    fun AccessibilityNodeInfo.findNode(query: QueryNode, skip: Int = -1): AccessibilityNodeInfo? {
        val matchedList = findNodes(query)
        if (skip >= 0) {
            return matchedList
                .filterIndexed { index, _ -> index > skip }
                .firstOrNull()
        }
        return matchedList.firstOrNull()
    }

    private fun QueryNode.traverseTopToBottom(
        nodeInfo: AccessibilityNodeInfo,
        currentNodeIndex: Int? = null
    ): AccessibilityNodeInfo? {
        val isMatched = params.map { param ->
            matchWithParamType(param, nodeInfo, currentNodeIndex)
        }.reduceOrNull { acc, b -> acc && b } ?: false
        if (isMatched) {
            if (child == null) return nodeInfo
            var resultNode: AccessibilityNodeInfo? = null
            nodeInfo.forEachChild { nodeIndex, childNode ->
                val matched = child.traverseTopToBottom(childNode!!, nodeIndex)
                if (matched != null) {
                    resultNode = matched
                    return@forEachChild
                }
            }
            return resultNode
        }
        return null
    }

    private fun matchWithParamType(
        param: QueryParam,
        nodeInfo: AccessibilityNodeInfo?,
        nodeInfoIndex: Int? = null
    ): Boolean {
        when (param.paramType) {
            ParamType.actions -> {
                val paramActions: List<AccessibilityNodeInfo.AccessibilityAction> =
                    Json.decodeFromString(param.value ?: "[]")
                return paramActions
                    .map { nodeInfo?.actionList?.contains(it) }
                    .reduceOrNull { acc, b -> acc == true && b == true } ?: false
            }
            else -> {}
        }

        return when (param.paramType) {
            ParamType.packageName -> nodeInfo?.packageName?.toString()
            ParamType.className -> nodeInfo?.className?.toString()
            ParamType.text -> nodeInfo?.text?.toString()
            ParamType.error -> nodeInfo?.error?.toString()
            ParamType.maxTextLength -> nodeInfo?.maxTextLength?.toString()
            ParamType.contentDescription -> nodeInfo?.contentDescription?.toString()
            ParamType.viewIdResName -> nodeInfo?.viewIdResourceName?.toString()
            ParamType.checkable -> nodeInfo?.isCheckable?.toString()
            ParamType.checked -> nodeInfo?.isChecked?.toString()
            ParamType.focusable -> nodeInfo?.isFocusable?.toString()
            ParamType.focused -> nodeInfo?.isFocused?.toString()
            ParamType.selected -> nodeInfo?.isSelected?.toString()
            ParamType.clickable -> nodeInfo?.isClickable?.toString()
            ParamType.longClickable -> nodeInfo?.isLongClickable?.toString()
            ParamType.enabled -> nodeInfo?.isEnabled?.toString()
            ParamType.password -> nodeInfo?.isPassword?.toString()
            ParamType.scrollable -> nodeInfo?.isScrollable?.toString()
            ParamType.visible -> nodeInfo?.isVisibleToUser?.toString()
            ParamType.nodeIndex -> nodeInfoIndex?.toString()
            else -> null
        }?.isMatched(param.value) ?: false
    }

    fun AccessibilityNodeInfo.forEachChild(block: (Int, AccessibilityNodeInfo?) -> Unit) {
        for (index in 0 until childCount) {
            block(index, getChild(index))
        }
    }
}
