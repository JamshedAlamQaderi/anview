package com.jamshedalamqaderi.anview.dsl

import com.jamshedalamqaderi.anview.entities.QueryNode
import com.jamshedalamqaderi.anview.entities.QueryParam
import com.jamshedalamqaderi.anview.enums.ParamType

fun anViewQuery(block: AnViewQueryBuilder.() -> Unit): QueryNode {
    return AnViewQueryNodeBuilder()
        .apply(block)
        .build()
}

class AnViewQueryNodeBuilder : AnViewBuilder<QueryNode>, AnViewQueryBuilder {
    private lateinit var queryParamBuilder: AnViewQueryParamBuilder
    private var queryNodeBuilder: AnViewQueryNodeBuilder? = null

    override fun params(block: AnViewParamBuilder.() -> Unit) {
        queryParamBuilder = AnViewQueryParamBuilder()
            .apply(block)
    }

    override fun query(block: AnViewQueryBuilder.() -> Unit) {
        queryNodeBuilder = AnViewQueryNodeBuilder().apply(block)
    }

    override fun build(): QueryNode {
        return QueryNode(queryParamBuilder.build(), child = queryNodeBuilder?.build())
    }

}

class AnViewQueryParamBuilder : AnViewBuilder<List<QueryParam>>, AnViewParamBuilder {
    private val paramList = arrayListOf<QueryParam>()

    override fun param(paramType: ParamType, value: String?) {
        paramList.add(QueryParam(paramType, value))
    }

    override fun build(): List<QueryParam> {
        return paramList
    }

}

@AnViewDSL
interface AnViewParamBuilder {
    fun param(paramType: ParamType, value: String?)
}

@AnViewDSL
interface AnViewQueryBuilder {
    fun params(block: AnViewParamBuilder.() -> Unit)
    fun query(block: AnViewQueryBuilder.() -> Unit)
}

interface AnViewBuilder<T> {
    fun build(): T
}

@DslMarker
annotation class AnViewDSL