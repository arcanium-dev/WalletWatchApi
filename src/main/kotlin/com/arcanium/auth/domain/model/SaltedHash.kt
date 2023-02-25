package com.arcanium.auth.domain.model

data class SaltedHash(
    val hash: String,
    val salt: String
)
