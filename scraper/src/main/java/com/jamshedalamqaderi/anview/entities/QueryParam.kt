package com.jamshedalamqaderi.anview.entities

import com.jamshedalamqaderi.anview.enums.ParamType
import kotlinx.serialization.Serializable

@Serializable
data class QueryParam(
    val paramType: ParamType,
    val value: String?
)