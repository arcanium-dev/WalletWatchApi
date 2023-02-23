package com.arcanium.auth.domain.repository

import com.arcanium.auth.data.entity.MongoUserEntity
import com.arcanium.auth.domain.model.User

interface UserDataRepository {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertNewUser(mongoUserEntity: MongoUserEntity): Boolean
    suspend fun getUserByUserId(userId: String): User?
}