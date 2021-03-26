package com.example.demo

import org.springframework.stereotype.Service

@Service
class AuthenticationService {
    fun validateToken(token: String?) {}

    class InvalidTokenException: RuntimeException()
}
