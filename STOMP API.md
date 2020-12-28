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
[LocationChangeEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)

---
# Перемещение в другую локацию
---

## Endpoint
`/players/move`

## Parameters
```
data class PlayerMoveRequest(
    @JsonProperty("1")
    var locationId: Short
)
```

## Result queue
`/user/queue/location`

## Result type
[LocationChangeEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)

---

# Запрос содержимого инвентаря
---

## Endpoint
`/players/request/inventory`

## Parameters
-

## Result queue
`/user/queue/inventory`

#Result type
[InventoryChangedEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)

# Запрос текущего снаряжения
---

## Endpoint
`/players/request/equipment`

## Parameters
-

## Result queue
`/user/queue/equipment`

#Result type
[EquipmentChangedEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)

---

# Атака гнезда мобов
---

## Endpoint
`/players/kick-nest`

## Parameters
```
data class PlayerKickMonsterNestRequest(
    @JsonProperty("1")
    var nestId: Short
)
```

## Result queue
not implemented yet

#Result type
not implemented yet

---

# Одеть предмет из инвентаря в снаряжение
---

## Endpoint
`/players/equip-item`

## Parameters
```
data class PlayerEquipItemRequest(
    @JsonProperty("1")
    var itemId: Long,
    @JsonProperty("2")
    var slotType: EquipmentSlotTypes
)
```

## Result queue
`/user/queue/inventory`
`/user/queue/equipment`

#Result type
[EquipmentChangedEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)
[InventoryChangedEvent](https://github.com/bogdanovan/LoggerWorld/blob/main/src/main/kotlin/io/github/loggerworld/messagebus/event/ResponseEvents.kt)
