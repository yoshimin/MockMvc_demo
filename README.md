### MockMvc_demo
Implement test SpringBoot's Controller using MockMvc.<br>
https://spring.io/guides/gs/testing-web/

Build request using `MockMvcRequestBuilders` and verify the status codes and response body using `MockMvcResultMatchers`.

```kotlin
val request = get("/greeting?name=hoge")
    .header(AUTHORIZATION, "Bearer xxxxxx")
mockMvc.perform(request)
    .andExpect(MockMvcResultMatchers.status().isOk)
    .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"content\":\"Hello, hoge!\"}"))
```

<br>
<br>

Test exception handling logic that using `@RestControllerAdvice` annotation.

```kotlin
@RestControllerAdvice
class DemoControllerAdvice {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Throwable): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error")
    }
}
```
Instantiate MockMvc with `MockMvcBuilders.standaloneSetup` method and set ControllerAdvice I want to test.

```kotlin
private val demoService = mock(DemoService::class.java)
private val demoController = DemoController(demoService)
private val mockMvc = MockMvcBuilders
        .standaloneSetup(demoController)
        .setControllerAdvice(DemoControllerAdvice()) <- here!!
        .build()
```

Mock `demoService.greeting` to throw exception and then `DemoControllerAdvice.handleException` will be called.
```kotlin
`when`(demoService.greeting("hoge")).thenThrow(RuntimeException())
val request = get("/greeting?name=hoge")
    .header(AUTHORIZATION, "Bearer xxxxxx")
mockMvc.perform(request)
    .andExpect(MockMvcResultMatchers.status().isInternalServerError)
    .andExpect(MockMvcResultMatchers.content().json("{\"errorCode\":500,\"message\":\"Internal Server Error\"}"))
```
<br>
<br>
Test Spring AOP logic.

```kotlin
@Aspect
@Component
class AuthenticationAspect(private val authenticationService: AuthenticationService) {
    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    fun validate() {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        authenticationService.validateToken(attributes.request.getHeader(AUTHORIZATION))
    }
}
```
`AspectJProxyFactory` class can create a proxy for a target object that is advised by aspects.<br>
Use MockMvc that instantiated with the proxy created by `AspectJProxyFactory` and then the method defined in AOP is called.

```kotlin
private val authenticationService = mock(AuthenticationService::class.java)
private val factory = AspectJProxyFactory(demoController)
        .apply {
            addAspect(AuthenticationAspect(authenticationService))
        }
private val proxy = factory.getProxy<DemoController>()

private val mockMvc = MockMvcBuilders
        .standaloneSetup(proxy) <- here!!
        .setControllerAdvice(DemoControllerAdvice())
        .build()
```
