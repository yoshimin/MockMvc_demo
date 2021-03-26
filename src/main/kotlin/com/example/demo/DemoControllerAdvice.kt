package com.example.demo

import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ErrorResponse(
        val errorCode: Int,
        val message: String
)

@RestControllerAdvice
class DemoControllerAdvice {
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: MissingServletRequestParameterException): ErrorResponse {
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Required String parameter '${e.parameterName}' is not present")
    }

    @ExceptionHandler(AuthenticationService.InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: AuthenticationService.InvalidTokenException): ErrorResponse {
        return ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token")
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleException(e: HttpRequestMethodNotSupportedException): ErrorResponse {
        return ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "Request method '${e.method}' not supported.")
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Throwable): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error")
    }
}
