package com.arcanium.auth.data.entity

data class SaltedHash(
    val hash: String,
    val salt: String
)
