package com.arcanium.auth.domain.service

import com.arcanium.auth.domain.model.TokenClaim
import com.arcanium.auth.domain.model.TokenConfig

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}