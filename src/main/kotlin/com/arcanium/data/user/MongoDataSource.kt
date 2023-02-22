package com.arcanium.data.user

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoDataSource(
    db: CoroutineDatabase
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserByUsername(username: String): User? {
        return users.findOne(User::userName eq username)
    }

    override suspend fun insertNewUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUserId(userId: String): User? {
        return users.findOne(User::id eq ObjectId(userId))
    }
}