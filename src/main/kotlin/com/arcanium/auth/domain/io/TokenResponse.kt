package com.arcanium.auth.domain.io

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)
