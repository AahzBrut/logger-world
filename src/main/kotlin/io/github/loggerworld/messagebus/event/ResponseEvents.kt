package io.github.loggerworld.messagebus.event

import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.MonsterClasses
import io.github.loggerworld.domain.enums.MonsterTypes
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.dto.response.monster.MobNestResponse
import io.github.loggerworld.dto.response.monster.MonsterShortResponse

sealed class NotificationEvent : Pool.Poolable

data class LocationChangedEvent(

    var locationId: Short = -1,
    var players: MutableList<ShortPlayerResponse> = mutableListOf(),
    var mobs: MutableList<MonsterShortResponse> = mutableListOf(),
    var mobNests: MutableList<MobNestResponse> = mutableListOf(),

) : NotificationEvent() {

    override fun reset() {
        locationId = -1
        players.clear()
        mobs.clear()
        mobNests.clear()
    }
}

data class WrongCommandEvent(
    var playerId: Long = -1,
    var message: String = ""
): NotificationEvent() {

    override fun reset() {
        playerId = -1
        message = ""
    }
}

data class SerializeItemsDropFromMobCommand(
    var playerId: Long = -1,
    var monsterClass: MonsterClasses = MonsterClasses.NOTHING,
    var monsterType: MonsterTypes = MonsterTypes.NOTHING,
    var monsterLevel: Byte = 0,
    var items: List<ItemData> = emptyList()

) : NotificationEvent() {

    override fun reset() {
        playerId = -1
        monsterClass = MonsterClasses.NOTHING
        monsterType = MonsterTypes.NOTHING
        monsterLevel = 0
        items = emptyList()
    }
}
