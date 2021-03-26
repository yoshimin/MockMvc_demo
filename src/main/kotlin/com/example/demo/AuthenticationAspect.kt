package com.example.demo

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Aspect
@Component
class AuthenticationAspect(private val authenticationService: AuthenticationService) {
    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    fun validate() {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        authenticationService.validateToken(attributes.request.getHeader(AUTHORIZATION))
    }
}
