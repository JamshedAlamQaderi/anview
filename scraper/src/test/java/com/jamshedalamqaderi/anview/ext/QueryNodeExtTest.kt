package com.jamshedalamqaderi.anview.ext

import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType
import com.jamshedalamqaderi.anview.ext.QueryNodeExt.toList
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryNodeExtTest {

    @Test
    fun traverseQueryNodeToListWithTopToBottom() {
        val queryNode = QueryNode(
            params = listOf(QueryParam(ParamType.nodeIndex, "0")),
            child = QueryNode(params = listOf(QueryParam(ParamType.nodeIndex, "1")))
        ).toList()
        assertEquals(
            queryNode,
            listOf(
                QueryNode(
                    params = listOf(QueryParam(ParamType.nodeIndex, "0"))
                ),
                QueryNode(
                    params = listOf(QueryParam(ParamType.nodeIndex, "1"))
                )
            )
        )
    }
}
