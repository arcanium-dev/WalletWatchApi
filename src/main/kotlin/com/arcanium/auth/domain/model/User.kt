package com.arcanium.auth.domain.model

data class User(
    val username: String,
    val password: String,
    val salt: String,
    val id: String
)
