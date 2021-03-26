package com.example.demo

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.bind.annotation.*

@RestController
class DemoController(private val demoService: DemoService) {
    @GetMapping("/greeting")
    fun greeting(
            @RequestHeader(AUTHORIZATION) accessToken: String?,
            @RequestParam name: String
    ): Greeting {
        return demoService.greeting(name)
    }

    @PostMapping("/reset")
    fun reset(@RequestHeader(AUTHORIZATION) accessToken: String?) {
        demoService.reset()
    }
}
