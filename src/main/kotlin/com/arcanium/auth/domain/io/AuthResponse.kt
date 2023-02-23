package com.arcanium.auth.domain.io

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
