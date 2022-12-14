package com.jamshedalamqaderi.anview.entities

import kotlinx.serialization.Serializable

@Serializable
data class QueryNode(
    val params: List<QueryParam>,
    val child: QueryNode? = null
)