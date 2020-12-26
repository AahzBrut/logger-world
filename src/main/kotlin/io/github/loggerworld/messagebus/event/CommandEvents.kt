package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.dto.inner.item.ItemData

sealed class CommandEvent : Pool.Poolable

data class PlayerStartCommand(
    var userId: Long = -1,
    var playerId: Long = -1,
    var locationId: Short = -1,
    var name: String = "",
    var classId: Byte = -1,
    var level: Byte = -1
) : CommandEvent() {

    override fun reset() {
        userId = -1
        playerId = -1
        locationId = -1
        name=""
        classId=-1
        level=-1
    }
}

data class PlayerMoveCommand(
    var playerId: Long = -1,
    var locationId: Short = -1,
) : CommandEvent() {

    override fun reset() {
        playerId = -1
        locationId = -1
    }
}

data class PlayerKickMonsterNestCommand(
    var playerId: Long = -1,
    var monsterNestId: Short = -1,
) : CommandEvent() {

    override fun reset() {
        playerId = -1
        monsterNestId = -1
    }
}

data class DeserializeItemsDropFromMobCommand(
    var playerId: Long = -1,
    var monsterClass: MonsterClasses = MonsterClasses.NOTHING,
    var monsterType: MonsterTypes = MonsterTypes.NOTHING,
    var monsterLevel: Byte = 0,
    var items: List<ItemData> = emptyList()

) : CommandEvent() {

    override fun reset() {
        playerId = -1
        monsterClass = MonsterClasses.NOTHING
        monsterType = MonsterTypes.NOTHING
        monsterLevel = 0
        items = emptyList()
    }
}

data class PlayerEquipItemCommand(
    var playerId: Long = -1,
    var itemId: Long = -1,
    var slotType: EquipmentSlotTypes = EquipmentSlotTypes.NOTHING
) : CommandEvent() {

    override fun reset() = Unit
}
