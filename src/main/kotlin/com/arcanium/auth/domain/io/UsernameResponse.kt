package com.arcanium.auth.domain.io

import kotlinx.serialization.Serializable

@Serializable
data class UsernameResponse(
    val username: String
)
