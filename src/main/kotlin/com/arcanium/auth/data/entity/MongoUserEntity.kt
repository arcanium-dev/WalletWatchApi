package com.arcanium.auth.data.entity

import com.arcanium.auth.domain.model.User
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class MongoUserEntity(
    val userName: String,
    val password: String,
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
) {
    fun toUser(): User {
        return User(
            username = this.userName,
            password = this.password,
            salt = this.salt,
            id = this.id.toHexString()
        )
    }
}