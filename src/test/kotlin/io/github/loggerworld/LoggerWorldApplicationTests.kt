package io.github.loggerworld

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.loggerworld.controller.SIGN_UP_URL
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.dto.event.LocationArriveEvent
import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.PlayerStartGameRequest
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.dto.response.ChatMessageResponse
import io.github.loggerworld.dto.response.character.PlayerClassesResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.TOKEN_PREFIX
import io.github.loggerworld.util.WS_CHAT
import io.github.loggerworld.util.WS_CONNECTION_POINT
import io.github.loggerworld.util.WS_DESTINATION_PREFIX
import io.github.loggerworld.util.WS_DS_PLAYER_CLASSES_MESSAGES
import io.github.loggerworld.util.WS_DS_PLAYER_MESSAGES
import io.github.loggerworld.util.WS_DS_TOPIC_MESSAGES
import io.github.loggerworld.util.WS_GAMEPLAY_EVENTS_QUEUE
import io.github.loggerworld.util.WS_PLAYERS_CLASSES_GET_ALL
import io.github.loggerworld.util.WS_PLAYERS_GET_ALL
import io.github.loggerworld.util.WS_PLAYERS_NEW
import io.github.loggerworld.util.WS_PLAYERS_START
import io.github.loggerworld.util.logger
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.WebSocketHttpHeaders
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
    private lateinit var token: String
    private var wsConnectionEstablished = false
    private var wsMessageReceived = false

    @Test
    fun contextLoads() {
        addNewUser()
        loginNewUser()
        connectToWebSocket()
        sendChatMessage()
        getCharacterClasses()
        addNewPlayer()
        getUserPlayers()
        startGameForPlayer()
    }

    private fun startGameForPlayer() {
        stompSession.send(WS_DESTINATION_PREFIX + WS_PLAYERS_START, getStartGameRequest())
        TimeUnit.MILLISECONDS.sleep(1000)
    }

    private fun addNewPlayer() {
        stompSession.send(WS_DESTINATION_PREFIX + WS_PLAYERS_NEW, getNewPlayerRequest())
        TimeUnit.MILLISECONDS.sleep(1000)
    }

    private fun getUserPlayers() {
        stompSession.send(WS_DESTINATION_PREFIX + WS_PLAYERS_GET_ALL, "")
        TimeUnit.MILLISECONDS.sleep(1000)
    }

    private fun getCharacterClasses() {
        stompSession.send(WS_DESTINATION_PREFIX + WS_PLAYERS_CLASSES_GET_ALL, "")
        TimeUnit.MILLISECONDS.sleep(100)
    }

    private fun sendChatMessage() {
        stompSession.send(WS_DESTINATION_PREFIX + WS_CHAT, getChatMessageRequest())
        waitTillMessageReceived()
    }

    private fun waitTillMessageReceived() {
        if (!wsMessageReceived) TimeUnit.MILLISECONDS.sleep(100)
        assert(wsMessageReceived)
    }

    fun connectToWebSocket() {

        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()
        val webSocketHttpHeaders = WebSocketHttpHeaders()
        webSocketHttpHeaders.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))
        val stompHeader = StompHeaders()
        stompHeader.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))

        stompClient.connect("ws://localhost:$port$WS_CONNECTION_POINT", webSocketHttpHeaders, stompHeader, sessionHandler)

        waitTillConnectionEstablished()
    }

    private fun waitTillConnectionEstablished() {
        if (!wsConnectionEstablished) TimeUnit.MILLISECONDS.sleep(300)
        assert(wsConnectionEstablished)
    }

    // Stomp client
    inner class MyStompSessionHandler : StompSessionHandler, LogAware {

        override fun getPayloadType(headers: StompHeaders): Type {

            val payloadType = when (headers.destination) {
                WS_DS_TOPIC_MESSAGES -> ChatMessageResponse::class.java
                WS_DS_PLAYER_MESSAGES -> PlayersResponse::class.java
                "/user/queue" -> LocationArriveEvent::class.java
                else -> PlayerClassesResponse::class.java
            }

            logger().info("Payload type for ${headers.destination} is: ${payloadType.simpleName}")

            return payloadType

        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {

            when (payload) {
                is ChatMessageResponse -> {
                    assert(payload.from == getChatMessageRequest().from)
                    assert(payload.message == getChatMessageRequest().message)
                    wsMessageReceived = true
                }
                is PlayerClassesResponse -> {
                    assert(payload.playerClasses.isNotEmpty())
                }
                is PlayersResponse -> {
                    assert(payload.players.isNotEmpty())
                }
            }
            logger().info("Got payload over web socket:\n$payload")
        }

        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            logger().info("WebSocket connected")
            session.subscribe(WS_DS_TOPIC_MESSAGES, this)
            session.subscribe(WS_DS_PLAYER_CLASSES_MESSAGES, this)
            session.subscribe(WS_DS_PLAYER_MESSAGES, this)
            session.subscribe("/user$WS_GAMEPLAY_EVENTS_QUEUE", this)

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
        assert(responseEntity.headers[HttpHeaders.AUTHORIZATION]?.any {
            token = it
            it.startsWith(TOKEN_PREFIX)
        }!!)
        logger().info("Token: ${responseEntity.headers[HttpHeaders.AUTHORIZATION]?.get(0)}")
    }

    fun addNewUser() {
        val responseEntity = restTemplate.postForEntity("http://localhost:$port$SIGN_UP_URL", getUserAddRequest(), Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.CREATED)
    }


    private fun getUserAddRequest(): UserAddRequest =
        UserAddRequest(
            "testUser",
            "pwd123",
            Languages.EN,
            "Test User",
            "testuser@mail.ru")

    private fun getUserLoginRequest(): UserLoginRequest =
        UserLoginRequest(
            "testUser",
            "pwd123"
        )

    private fun getChatMessageRequest(): ChatMessageRequest =
        ChatMessageRequest("Uncle Bob", "Hello!")

    private fun getNewPlayerRequest(): PlayerAddRequest =
        PlayerAddRequest("Superman", PlayerClasses.WARRIOR)

    private fun getStartGameRequest(): PlayerStartGameRequest {
        return PlayerStartGameRequest(
            playerId = 1
        )
    }
}
