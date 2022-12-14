package com.jamshedalamqaderi.anview.entities

import android.view.accessibility.AccessibilityNodeInfo
import com.jamshedalamqaderi.anview.enums.ParamType
import com.jamshedalamqaderi.anview.ext.QueryNodeExt.traverse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class QueryNodeTest {
    @Mock
    private lateinit var node1: AccessibilityNodeInfo

    @Mock
    private lateinit var child1: AccessibilityNodeInfo

    @Mock
    private lateinit var child2: AccessibilityNodeInfo

    @Before
    fun setup() {
        child1 = mock {
            on(mock.packageName) doReturn "com.jamshedalamqaderi.AnViewChild1"
            on(mock.className) doReturn "android.widget.TextView"
            on(mock.text) doReturn "I am child1"
        }

        child2 = mock {
            on(mock.packageName) doReturn "com.jamshedalamqaderi.AnViewChild2"
            on(mock.className) doReturn "android.widget.TextView"
            on(mock.text) doReturn "I am child2"
        }

        node1 = mock {
            on(mock.packageName) doReturn "com.jamshedalamqaderi.AnView"
            on(mock.className) doReturn "android.widget.View"
            on(mock.childCount) doReturn 2
            on(mock.getChild(0)) doReturn child1
            on(mock.getChild(1)) doReturn child2
        }

    }

    @Test
    fun `Given QueryNode with single node info when query matched with AccessibilityNodeInfo then should return 2 child node`() {
        val queryNode = QueryNode(
            params = listOf(
                QueryParam(
                    ParamType.packageName,
                    "com.jamshedalamqaderi.AnView"
                ),
                QueryParam(
                    ParamType.className,
                    "android.widget.View"
                ),
            ),
        )
        val nodes = queryNode.traverse(node1)
        assertEquals(nodes.size, 1)
        val nodeInfo = nodes.first()
        assertEquals(nodeInfo.childCount, 2)
        assertEquals(nodeInfo.getChild(0)?.text, "I am child1")
        assertEquals(nodeInfo.getChild(1)?.text, "I am child2")
    }

    @Test
    fun `Given className param to the traverse algo when traversed then should return with two child node in a list`() {
        val queryNode = QueryNode(
            params = listOf(
                QueryParam(
                    ParamType.packageName,
                    "com.jamshedalamqaderi.AnView"
                ),
                QueryParam(
                    ParamType.className,
                    "android.widget.View"
                ),
            ),
            child = QueryNode(
                params = listOf(
                    QueryParam(
                        ParamType.className,
                        "android.widget.TextView"
                    ),
                )
            )
        )
        val traversedList = queryNode.traverse(node1)
        assertEquals(traversedList.size, 2)
        assertEquals(traversedList[0].text, "I am child1")
        assertEquals(traversedList[1].text, "I am child2")
    }

    @Test
    fun `Given className and index param to the traverse algo when traversed then should return with child2`() {
        val queryNode = QueryNode(
            params = listOf(
                QueryParam(
                    ParamType.packageName,
                    "com.jamshedalamqaderi.AnView"
                ),
                QueryParam(
                    ParamType.className,
                    "android.widget.View"
                ),
            ),
            child = QueryNode(
                params = listOf(
                    QueryParam(
                        ParamType.className,
                        "android.widget.TextView"
                    ),
                    QueryParam(
                        ParamType.nodeIndex,
                        "1"
                    ),
                )
            )
        )
        val traversedList = queryNode.traverse(node1)
        assertEquals(traversedList.size, 1)
        assertEquals(traversedList[0].text, "I am child2")
    }
}