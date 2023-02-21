package com.arcanium.security.token

// Used to store information in token
data class TokenClaim(
    val name: String,
    val value: String
)
