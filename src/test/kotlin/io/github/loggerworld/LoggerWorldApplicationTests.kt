package io.github.loggerworld

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.dto.response.ChatMessageResponse
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.TOKEN_PREFIX
import io.github.loggerworld.util.logger
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    locations = ["classpath:application-integrationtest.properties"])
class LoggerWorldApplicationTests : LogAware {

    @LocalServerPort
    private var port = 8080
    private final val restTemplate = RestTemplate()
    private final val client: WebSocketClient = StandardWebSocketClient()
    private final val stompClient = WebSocketStompClient(client)
    private lateinit var stompSession: StompSession
    private var wsConnectionEstablished = false
    private var wsMessageReceived = false

    @Test
    fun contextLoads() {
        addNewUser()
        loginNewUser()
        connectToWebSocket()
        sendChatMessage()
    }

    private fun sendChatMessage() {
        stompSession.send("/app/chat", getChatMessageRequest())
        waitTillMessageReceived()
    }

    private fun waitTillMessageReceived() {
        while (!wsMessageReceived) TimeUnit.MILLISECONDS.sleep(10)
        assert(wsMessageReceived)
    }

    fun connectToWebSocket() {

        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        stompClient.connect("ws://localhost:$port/chat", sessionHandler)

        waitTillConnectionEstablished()
    }

    private fun waitTillConnectionEstablished() {
        while (!wsConnectionEstablished) TimeUnit.MILLISECONDS.sleep(10)
        assert(wsConnectionEstablished)
    }

    // Stomp client
    inner class MyStompSessionHandler : StompSessionHandler, LogAware {

        override fun getPayloadType(headers: StompHeaders): Type {
            return ChatMessageResponse::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            assert(payload is ChatMessageResponse)
            if (payload is ChatMessageResponse) {
                assert(payload.from == getChatMessageRequest().from)
                assert(payload.message == getChatMessageRequest().message)
                wsMessageReceived = true
            }
            logger().info("Got payload over web socket:\n$payload")
        }

        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            logger().info("WebSocket connected")
            session.subscribe("/topic/messages", this)
            wsConnectionEstablished = true
            stompSession = session
        }

        override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
            logger().info("WebSocket got exception: ${exception.message}")
        }

        override fun handleTransportError(session: StompSession, exception: Throwable) {
            logger().info("WebSocket got transport error: ${exception.message}")
        }
    }

    fun loginNewUser() {
        val responseEntity = restTemplate.postForEntity("http://localhost:$port/api/user/login", getUserLoginRequest(), Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.OK)
        assert(responseEntity.headers["Authorization"]?.any {
            it.startsWith(TOKEN_PREFIX)
        }!!)
        logger().info("Token: ${responseEntity.headers["Authorization"]?.get(0)}")
    }

    fun addNewUser() {
        val responseEntity = restTemplate.postForEntity("http://localhost:$port/api/user/sign-up", getUserAddRequest(), Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.CREATED)
    }


    fun getUserAddRequest(): UserAddRequest =
        UserAddRequest(
            "testUser",
            "pwd123",
            Languages.EN,
            "Test User",
            "testuser@mail.ru")

    fun getUserLoginRequest(): UserLoginRequest =
        UserLoginRequest(
            "testUser",
            "pwd123"
        )

    private fun getChatMessageRequest(): ChatMessageRequest =
        ChatMessageRequest("Uncle Bob", "Hello!")
}
