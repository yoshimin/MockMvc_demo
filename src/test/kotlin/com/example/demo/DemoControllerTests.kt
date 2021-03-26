package com.example.demo

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class DemoControllerTests {
    private val demoService = mock(DemoService::class.java)
    private val demoController = DemoController(demoService)

    private val authenticationService = mock(AuthenticationService::class.java)
    private val factory = AspectJProxyFactory(demoController)
            .apply {
                addAspect(AuthenticationAspect(authenticationService))
            }
    private val proxy = factory.getProxy<DemoController>()

    private val mockMvc = MockMvcBuilders
            .standaloneSetup(proxy)
            .setControllerAdvice(DemoControllerAdvice())
            .build()


    @BeforeEach
    fun setup() {
        clearInvocations(demoService)
        clearInvocations(authenticationService)
    }

    @Test
    fun greeting() {
        `when`(demoService.greeting("hoge")).thenReturn(Greeting(1, "Hello, hoge!"))
        val request = get("/greeting?name=hoge")
                .header(AUTHORIZATION, "Bearer xxxxxx")
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"content\":\"Hello, hoge!\"}"))
    }

    @Test
    fun reset() {
        mockMvc.perform(post("/reset"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    fun badRequest() {
        mockMvc.perform(get("/greeting"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().json("{\"errorCode\":400,\"message\":\"Required String parameter 'name' is not present\"}"))
    }

    @Test
    fun unauthorized() {
        `when`(authenticationService.validateToken("Bearer xxxxxx")).thenThrow(AuthenticationService.InvalidTokenException())
        val request = get("/greeting?name=hoge")
                .header(AUTHORIZATION, "Bearer xxxxxx")
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.content().json("{\"errorCode\":401,\"message\":\"Invalid token\"}"))
    }

    @Test
    fun methodNotAllowed() {
        mockMvc.perform(post("/greeting?name=hoge"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
                .andExpect(MockMvcResultMatchers.content().json("{\"errorCode\":405,\"message\":\"Request method 'POST' not supported.\"}"))
    }

    @Test
    fun internalServerError() {
        `when`(demoService.greeting("hoge")).thenThrow(RuntimeException())
        val request = get("/greeting?name=hoge")
                .header(AUTHORIZATION, "Bearer xxxxxx")
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.content().json("{\"errorCode\":500,\"message\":\"Internal Server Error\"}"))
    }
}
