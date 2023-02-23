package com.arcanium.auth.data.repository

import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.data.entity.UserEntity
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataRepository(
    db: CoroutineDatabase
) : UserDataRepository {

    private val users = db.getCollection<UserEntity>()

    override suspend fun getUserByUsername(username: String): UserEntity? {
        return users.findOne(UserEntity::userName eq username)
    }

    override suspend fun insertNewUser(userEntity: UserEntity): Boolean {
        return users.insertOne(userEntity).wasAcknowledged()
    }

    override suspend fun getUserByUserId(userId: String): UserEntity? {
        return users.findOne(UserEntity::id eq ObjectId(userId))
    }
}