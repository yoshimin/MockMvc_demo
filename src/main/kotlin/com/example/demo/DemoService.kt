package com.example.demo

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class DemoService {
    private val counter = AtomicLong()

    fun greeting(name: String): Greeting {
        return Greeting(counter.incrementAndGet(), "Hello, $name!")
    }

    fun reset() {
        counter.set(0)
    }
}
