package com.jamshedalamqaderi.anview.ext

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object QueryNodeExt {
    fun QueryNode.toList(): List<QueryNode> {
        val childList = child?.toList()
        if (childList != null && childList.isNotEmpty()) {
            return listOf(this.copy(child = null), *childList.toTypedArray())
        }
        return listOf(this.copy(child = null))
    }

    fun QueryNode.traverse(nodeInfo: AccessibilityNodeInfo?): List<AccessibilityNodeInfo> {
        val matchedNodeList = arrayListOf<AccessibilityNodeInfo>()
        traverse(nodeInfo, null, matchedNodeList)
        return matchedNodeList
    }

    private fun QueryNode.traverse(
        nodeInfo: AccessibilityNodeInfo?,
        nodeInfoIndex: Int?,
        matchedNodeList: ArrayList<AccessibilityNodeInfo>,
    ) {
        val isMatched = params.map { param ->
            matchWithParamType(param, nodeInfo, nodeInfoIndex)
        }.reduceOrNull { acc, b -> acc && b }
        if (isMatched == false) {
            matchedNodeList.clear()
            return
        }
        if (child == null && nodeInfo != null) {
            matchedNodeList.add(nodeInfo)
        }
        for (index in 0 until (nodeInfo?.childCount ?: 0)) {
            val nodeChild = nodeInfo?.getChild(index)
            child?.traverse(nodeChild, index, matchedNodeList)
        }
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
}
