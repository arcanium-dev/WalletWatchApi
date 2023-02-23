package com.arcanium.auth.data.repository

import com.arcanium.auth.data.entity.MongoUserEntity
import com.arcanium.auth.domain.model.User
import com.arcanium.auth.domain.repository.UserDataRepository
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataRepository(
    db: CoroutineDatabase
) : UserDataRepository {

    private val users = db.getCollection<MongoUserEntity>()

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(MongoUserEntity::userName eq username)?.toUser()
    }

    override suspend fun insertNewUser(mongoUserEntity: MongoUserEntity): Boolean {
        return users.insertOne(mongoUserEntity).wasAcknowledged()
    }

    override suspend fun getUserByUserId(userId: String): User? {
        return users.findOne(MongoUserEntity::id eq ObjectId(userId))?.toUser()
    }
}