package com.arcanium.auth.domain.service

import com.arcanium.auth.domain.model.SaltedHash

interface HashingService {

    // To be used for hashing passwords with some salt
    fun generateSaltedHash(value: String, saltLength: Int = 32): SaltedHash

    // Verify a specific hash i.e. password checking
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}