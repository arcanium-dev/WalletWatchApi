package com.arcanium.auth.domain.model

// Used to store information in token
data class TokenClaim(
    val name: String,
    val value: String
)
