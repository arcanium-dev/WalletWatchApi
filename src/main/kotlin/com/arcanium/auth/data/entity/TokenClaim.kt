package com.arcanium.auth.data.entity

// Used to store information in token
data class TokenClaim(
    val name: String,
    val value: String
)
