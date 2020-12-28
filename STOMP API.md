# Старт игры за выбранного персонажа:
---

## Endpoint
`/player/start`

## Parameters
```
data class PlayerStartGameRequest(
    @JsonProperty("1")
    var playerId: Long = -1)
```

## Result queue
`/user/queue/location`

## Result type
```
data class LocationChangedEvent(
    @JsonProperty("1")
    var locationId: Short = -1,
    @JsonProperty("2")
    var players: MutableList<ShortPlayerResponse> = mutableListOf(),
    @JsonProperty("3")
    var mobs: MutableList<MonsterShortResponse> = mutableListOf(),
    @JsonProperty("4")
    var mobNests: MutableList<MobNestResponse> = mutableListOf()
)
```
```
data class ShortPlayerResponse(
    @JsonProperty("1")
    var id: Long = -1,
    @JsonProperty("2")
    var name: String = "",
    @JsonProperty("3")
    var level: Byte = -1,
    @JsonProperty("4")
    var classId: Byte = -1,
    @JsonProperty("5")
    var state: States = States.IDLE
)
```
