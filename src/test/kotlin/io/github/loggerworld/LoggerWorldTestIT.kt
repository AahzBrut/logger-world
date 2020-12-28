package io.github.loggerworld

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.loggerworld.controller.LOCATIONS_URL
import io.github.loggerworld.controller.LOCATION_TYPES_URL
import io.github.loggerworld.controller.PERFORMANCE_COUNTERS_URL
import io.github.loggerworld.controller.PLAYERS_CLASSES_URL
import io.github.loggerworld.controller.PLAYERS_LOGS_URL
import io.github.loggerworld.controller.PLAYERS_URL
import io.github.loggerworld.controller.SIGN_UP_URL
import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.PlayerAttributeEnum
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.dto.request.ChatMessageRequest
import io.github.loggerworld.dto.request.PlayerAddRequest
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.dto.request.commands.PlayerEquipItemRequest
import io.github.loggerworld.dto.request.commands.PlayerKickMonsterNestRequest
import io.github.loggerworld.dto.request.commands.PlayerMoveRequest
import io.github.loggerworld.dto.request.commands.PlayerStartGameRequest
import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.dto.response.character.PlayerResponse
import io.github.loggerworld.dto.response.character.PlayersResponse
import io.github.loggerworld.dto.response.chat.ChatMessageResponse
import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.dto.response.logging.PlayerLogEntryResponse
import io.github.loggerworld.messagebus.event.EquipmentChangedEvent
import io.github.loggerworld.messagebus.event.InventoryChangedEvent
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.messagebus.event.WrongCommandEvent
import io.github.loggerworld.service.MonsterService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.PERSONAL
import io.github.loggerworld.util.TOKEN_PREFIX
import io.github.loggerworld.util.WS_CHAT
import io.github.loggerworld.util.WS_CONNECTION_POINT
import io.github.loggerworld.util.WS_DESTINATION_PREFIX
import io.github.loggerworld.util.WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_LOG_QUEUE
import io.github.loggerworld.util.WS_GAMEPLAY_WRONG_COMMAND_QUEUE
import io.github.loggerworld.util.WS_PLAYERS_EQUIP_ITEM
import io.github.loggerworld.util.WS_PLAYERS_KICK_NEST
import io.github.loggerworld.util.WS_PLAYERS_MOVE
import io.github.loggerworld.util.WS_PLAYERS_REQUEST_EQUIPMENT
import io.github.loggerworld.util.WS_PLAYERS_REQUEST_INVENTORY
import io.github.loggerworld.util.WS_PLAYERS_START
import io.github.loggerworld.util.WS_TOPIC_MESSAGES
import io.github.loggerworld.util.logger
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    locations = ["classpath:application-integrationtest.properties"])
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class LoggerWorldTestIT : LogAware {

    @Autowired
    private var monsterService: MonsterService? = null

    @LocalServerPort
    private var port = -1
    private final val restTemplate = RestTemplate()
    private final val signupUrl by lazy { "http://localhost:$port$SIGN_UP_URL" }
    private final val loginUrl by lazy { "http://localhost:$port/api/user/login" }
    private final val baseUrl by lazy {  "http://localhost:$port" }

    companion object {
        private var client1: WebSocketClient = StandardWebSocketClient()
        private var stompClient1: WebSocketStompClient = WebSocketStompClient(client1)
        private lateinit var stompSession1: StompSession
        private var client2: WebSocketClient = StandardWebSocketClient()
        private var stompClient2: WebSocketStompClient = WebSocketStompClient(client2)
        private lateinit var stompSession2: StompSession
    }

    //region static objects declaration
    val firstUserAddRequest = UserAddRequest(
        "testUser1",
        "pwd123",
        Languages.EN,
        "Test User 1",
        "testuser1@mail.ru")

    val secondUserAddRequest = UserAddRequest(
        "testUser2",
        "pwd123",
        Languages.RU,
        "Вася",
        "testuser2@mail.ru")

    val firstUserLoginRequest = UserLoginRequest(
        "testUser1",
        "pwd123"
    )

    val secondUserLoginRequest = UserLoginRequest(
        "testUser2",
        "pwd123"
    )
    //endregion

    @Test
    @Order(1)
    fun createFirstUser() {
        val responseEntity = restTemplate.postForEntity(signupUrl, firstUserAddRequest, Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.CREATED)
    }

    @Test
    @Order(2)
    fun createSecondUser() {
        val responseEntity = restTemplate.postForEntity(signupUrl, secondUserAddRequest, Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.CREATED)
    }

    @Test
    @Order(3)
    fun loginFirstUser() {
        val responseEntity = restTemplate.postForEntity(loginUrl, firstUserLoginRequest, Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.OK)
        assert(responseEntity.headers[HttpHeaders.AUTHORIZATION]?.any {
            it.startsWith(TOKEN_PREFIX)
        }!!)
    }

    @Test
    @Order(4)
    fun loginSecondUser() {
        val responseEntity = restTemplate.postForEntity(loginUrl, secondUserLoginRequest, Object::class.java)
        assert(responseEntity.statusCode == HttpStatus.OK)
        assert(responseEntity.headers[HttpHeaders.AUTHORIZATION]?.any {
            it.startsWith(TOKEN_PREFIX)
        }!!)
    }

    @Test
    @Order(5)
    fun connectToWsFirstUser() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient1.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        val token = getJwtToken(firstUserLoginRequest)!!

        val webSocketHttpHeaders = WebSocketHttpHeaders()
        webSocketHttpHeaders.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))
        val stompHeader = StompHeaders()
        stompHeader.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))

        stompClient1.connect("ws://localhost:$port$WS_CONNECTION_POINT", webSocketHttpHeaders, stompHeader, sessionHandler)
        TimeUnit.MILLISECONDS.sleep(500)
        assert(stompSession1.isConnected)
    }

    @Test
    @Order(6)
    fun connectToWsSecondUser() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient2.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        val token = getJwtToken(secondUserLoginRequest)!!

        val webSocketHttpHeaders = WebSocketHttpHeaders()
        webSocketHttpHeaders.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))
        val stompHeader = StompHeaders()
        stompHeader.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))

        stompClient2.connect("ws://localhost:$port$WS_CONNECTION_POINT", webSocketHttpHeaders, stompHeader, sessionHandler)
        TimeUnit.MILLISECONDS.sleep(500)
        assert(stompSession2.isConnected)
    }

    @Test
    @Order(7)
    fun firstUserSendChatMessage() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_CHAT, ChatMessageRequest("Uncle Bob", "Привет!"))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(8)
    fun secondUserSendChatMessage() {
        stompSession2.send(WS_DESTINATION_PREFIX + WS_CHAT, ChatMessageRequest("Uncle Sam", "Привет!"))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(9)
    fun firstUserGetCharacterClasses() {

        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + PLAYERS_CLASSES_URL, ResponseObject::class.java)
        logger().info("\n\nClasses for first user:\n${classes.body}")
    }

    @Test
    @Order(10)
    fun secondUserGetCharacterClasses() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + PLAYERS_CLASSES_URL, ResponseObject::class.java)
        logger().info("\n\nClasses for second user:\n${classes.body}")
    }

    @Test
    @Order(11)
    fun firstUserCreateNewPlayer() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val newPlayer = restTemplate1.postForEntity<PlayerResponse>(baseUrl + PLAYERS_URL, PlayerAddRequest("Superman", PlayerClasses.WARRIOR, mapOf(PlayerAttributeEnum.STR.ordinal.toByte() to 2f, PlayerAttributeEnum.AGI.ordinal.toByte() to 1f, PlayerAttributeEnum.CON.ordinal.toByte() to 2f)))
        logger().info(newPlayer.toString())
    }

    @Test
    @Order(12)
    fun secondUserCreateNewPlayer() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val newPlayer = restTemplate1.postForEntity<PlayerResponse>(baseUrl + PLAYERS_URL, PlayerAddRequest("Spiderman", PlayerClasses.ASSASSIN, mapOf(PlayerAttributeEnum.STR.ordinal.toByte() to 1f, PlayerAttributeEnum.AGI.ordinal.toByte() to 3f, PlayerAttributeEnum.CON.ordinal.toByte() to 1f)))
        logger().info(newPlayer.toString())
    }

    @Test
    @Order(13)
    fun firstUserGetPlayers() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + PLAYERS_URL, ResponseObject::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(14)
    fun secondUserGetPlayers() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + PLAYERS_URL, PlayersResponse::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(15)
    fun firstUserStartGame() {
        logger().info("\n\n First user starting the game")
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_START, PlayerStartGameRequest(1))
        TimeUnit.MILLISECONDS.sleep(1000)
        logger().info("\n\n First user started the game")
    }

    @Test
    @Order(16)
    fun firstUserDisconnect1() {
        logger().info("\n\n First user disconnecting")
        stompSession1.disconnect()
        TimeUnit.MILLISECONDS.sleep(1000)
        logger().info("\n\n First user disconnected")
    }

    @Test
    @Order(17)
    fun connectToWsFirstUser1() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient1.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        val token = getJwtToken(firstUserLoginRequest)!!

        val webSocketHttpHeaders = WebSocketHttpHeaders()
        webSocketHttpHeaders.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))
        val stompHeader = StompHeaders()
        stompHeader.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))

        stompClient1.connect("ws://localhost:$port$WS_CONNECTION_POINT", webSocketHttpHeaders, stompHeader, sessionHandler)
        TimeUnit.MILLISECONDS.sleep(500)
        assert(stompSession1.isConnected)
    }

    @Test
    @Order(18)
    fun firstUserStartGame1() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_START, PlayerStartGameRequest(1))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(19)
    fun secondUserStartGame() {
        stompSession2.send(WS_DESTINATION_PREFIX + WS_PLAYERS_START, PlayerStartGameRequest(2))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(20)
    fun firstUserMove() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_MOVE, PlayerMoveRequest(8))
        TimeUnit.MILLISECONDS.sleep(300)
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_MOVE, PlayerMoveRequest(5))
        TimeUnit.MILLISECONDS.sleep(7000)
    }

    @Test
    @Order(21)
    fun firstUserDisconnect2() {
        logger().info("\n\n First user disconnecting")
        stompSession1.disconnect()
        TimeUnit.MILLISECONDS.sleep(1000)
        logger().info("\n\n First user disconnected")
    }

    @Test
    @Order(22)
    fun connectToWsFirstUser2() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val jacksonMapper = MappingJackson2MessageConverter()
        jacksonMapper.objectMapper = objectMapper
        stompClient1.messageConverter = jacksonMapper

        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        val token = getJwtToken(firstUserLoginRequest)!!

        val webSocketHttpHeaders = WebSocketHttpHeaders()
        webSocketHttpHeaders.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))
        val stompHeader = StompHeaders()
        stompHeader.putAll(mutableMapOf(HttpHeaders.AUTHORIZATION to mutableListOf(token)))

        stompClient1.connect("ws://localhost:$port$WS_CONNECTION_POINT", webSocketHttpHeaders, stompHeader, sessionHandler)
        TimeUnit.MILLISECONDS.sleep(500)
        assert(stompSession1.isConnected)
    }

    @Test
    @Order(23)
    fun firstUserStartGame2() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_START, PlayerStartGameRequest(playerId = 1))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(24)
    fun secondUserMove() {
        stompSession2.send(WS_DESTINATION_PREFIX + WS_PLAYERS_MOVE, PlayerMoveRequest(7))
        TimeUnit.MILLISECONDS.sleep(7000)
    }

    @Test
    @Order(25)
    fun firstUserGetLocationTypesDictionary() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + LOCATION_TYPES_URL, LocationTypesResponse::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(26)
    fun secondUserGetLocationTypesDictionary() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + LOCATION_TYPES_URL, LocationTypesResponse::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(27)
    fun firstUserGetLocationsDictionary() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + LOCATIONS_URL, LocationsResponse::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(28)
    fun secondUserGetLocationsDictionary() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val classes = restTemplate1.getForEntity(baseUrl + LOCATIONS_URL, LocationsResponse::class.java)
        logger().info(classes.toString())
    }

    @Test
    @Order(29)
    fun firstUserKickNest() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_KICK_NEST, PlayerKickMonsterNestRequest(1))
        TimeUnit.MILLISECONDS.sleep(10000)
    }

    @Test
    @Order(30)
    fun firstUserEquipSword() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_EQUIP_ITEM, PlayerEquipItemRequest(1, EquipmentSlotTypes.RIGHT_ARM))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(31)
    fun secondUserKickNest() {
        stompSession2.send(WS_DESTINATION_PREFIX + WS_PLAYERS_KICK_NEST, PlayerKickMonsterNestRequest(3))
        stompSession2.send(WS_DESTINATION_PREFIX + WS_PLAYERS_KICK_NEST, PlayerKickMonsterNestRequest(3))
        TimeUnit.MILLISECONDS.sleep(15000)
    }

    @Test
    @Order(32)
    fun secondUserEquipSword() {
        stompSession2.send(WS_DESTINATION_PREFIX + WS_PLAYERS_EQUIP_ITEM, PlayerEquipItemRequest(3, EquipmentSlotTypes.RIGHT_ARM))
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(33)
    fun firstUserGetLogs() {
        val restTemplate1 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(firstUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val responseEntity = restTemplate1.getForEntity(baseUrl + PLAYERS_LOGS_URL, ResponseObject::class.java)
        logger().info("\n\nLogs for first user:\n${responseEntity.body}")
    }

    @Test
    @Order(34)
    fun secondUserGetLogs() {
        val restTemplate2 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val responseEntity= restTemplate2.getForEntity(baseUrl + PLAYERS_LOGS_URL, ResponseObject::class.java)
        logger().info("\n\nLogs for second user:\n${responseEntity.body}")
    }

    @Test
    @Order(35)
    fun firstUserRequestInventory() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_REQUEST_INVENTORY, "")
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(36)
    fun firstUserRequestEquipment() {
        stompSession1.send(WS_DESTINATION_PREFIX + WS_PLAYERS_REQUEST_EQUIPMENT, "")
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(37)
    fun getPerformanceCounters() {
        val restTemplate2 = RestTemplateBuilder(RestTemplateCustomizer {
            it.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", getJwtToken(secondUserLoginRequest))
                execution.execute(request, body)
            })
        }).build()

        val responseEntity= restTemplate2.getForEntity(baseUrl + PERFORMANCE_COUNTERS_URL, ResponseObject::class.java)
        logger().info("\n\nPerformance counters:\n${responseEntity.body}")
    }

    @Test
    @Order(38)
    fun firstUserDisconnect() {
        stompSession1.disconnect()
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(39)
    fun secondUserDisconnect() {
        stompSession2.disconnect()
        TimeUnit.MILLISECONDS.sleep(300)
    }

    @Test
    @Order(40)
    fun testMonsterSpawner() {
        val monsterSpawnerData = monsterService?.getMonsterSpawnerData()!!

        val monsterTypes = (1..10000).map {
            monsterSpawnerData
                .classes[MonsterClasses.GREY_RAT]!!
                .levels[1]!!
                .getRandomMonsterType()
        }.toList().groupBy {
            it
        }.entries.associate {
            it.key to it.value.size
        }

        logger().info("\n\n$monsterTypes")
    }

    private fun getJwtToken(userLoginRequest: UserLoginRequest): String? {
        val responseEntity = restTemplate.postForEntity(loginUrl, userLoginRequest, Object::class.java)
        return responseEntity.headers[HttpHeaders.AUTHORIZATION]?.get(0)
    }

        //region Stomp client handler
    inner class MyStompSessionHandler : StompSessionHandler, LogAware {

        override fun getPayloadType(headers: StompHeaders): Type {

            val payloadType = when (headers.destination) {
                WS_TOPIC_MESSAGES -> ChatMessageResponse::class.java
                PERSONAL + WS_GAMEPLAY_WRONG_COMMAND_QUEUE -> WrongCommandEvent::class.java
                PERSONAL + WS_GAMEPLAY_LOG_QUEUE -> PlayerLogEntryResponse::class.java
                PERSONAL + WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE -> InventoryChangedEvent::class.java
                PERSONAL + WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE -> EquipmentChangedEvent::class.java
                else -> LocationChangedEvent::class.java
            }

            logger().debug("\n\nUser received message, payload type for ${headers.destination} is: ${payloadType.simpleName}")

            return payloadType

        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {

            logger().info(headers.toSingleValueMap().toString())
            logger().info("\n\nGot payload over web socket:\n$payload")
        }

        override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
            logger().info("\n\nWebSocket connected")

            session.subscribe(WS_TOPIC_MESSAGES, this)
            session.subscribe(PERSONAL + WS_GAMEPLAY_LOCATION_NOTIFICATION_QUEUE, this)
            session.subscribe(PERSONAL + WS_GAMEPLAY_WRONG_COMMAND_QUEUE, this)
            session.subscribe(PERSONAL + WS_GAMEPLAY_LOG_QUEUE, this)
            session.subscribe(PERSONAL + WS_GAMEPLAY_INVENTORY_CHANGE_QUEUE, this)
            session.subscribe(PERSONAL + WS_GAMEPLAY_EQUIPMENT_CHANGE_QUEUE, this)

            when (connectedHeaders["user-name"][0]) {
                firstUserLoginRequest.userName -> stompSession1 = session
                secondUserLoginRequest.userName -> stompSession2 = session
            }
        }

        override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
            logger().info("WebSocket got exception: ${exception.message}")
        }

        override fun handleTransportError(session: StompSession, exception: Throwable) {
            logger().info("WebSocket got transport error: ${exception.message}")
        }
    }
    //endregion
}