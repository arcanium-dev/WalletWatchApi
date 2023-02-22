package com.arcanium.data.user

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertNewUser(user: User): Boolean
    suspend fun getUserByUserId(userId: String): User?
}