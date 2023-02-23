package com.arcanium.auth.data.io

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
