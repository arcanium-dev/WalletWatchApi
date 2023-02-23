package com.arcanium.auth.domain.repository

import com.arcanium.auth.data.entity.UserEntity

interface UserDataRepository {
    suspend fun getUserByUsername(username: String): UserEntity?
    suspend fun insertNewUser(userEntity: UserEntity): Boolean
    suspend fun getUserByUserId(userId: String): UserEntity?
}