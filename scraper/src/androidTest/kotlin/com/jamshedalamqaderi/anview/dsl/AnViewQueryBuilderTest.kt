package com.jamshedalamqaderi.anview.dsl

import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType
import org.junit.Assert.assertEquals
import org.junit.Test

class AnViewQueryBuilderTest {

    @Test
    fun `Test QueryBuilder dsl`() {
        val query = anViewQuery {
            params {
                param(ParamType.nodeIndex, "1")
            }
            query {
                params {
                    param(ParamType.text, "Hello, AnView")
                    param(ParamType.contentDescription, "Content Description")
                }
            }
        }

        assertEquals(
            query,
            QueryNode(
                params = listOf(QueryParam(ParamType.nodeIndex, "1")),
                child = QueryNode(
                    params = listOf(
                        QueryParam(ParamType.text, "Hello, AnView"),
                        QueryParam(ParamType.contentDescription, "Content Description")
                    )
                )
            )
        )
    }
}
