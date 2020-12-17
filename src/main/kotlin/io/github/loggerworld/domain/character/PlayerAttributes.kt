package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.PlayerAttributeEnum
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "character_attributes")
@AttributeOverride(name = "id", column = Column(name = "character_attributes_id"))
data class PlayerAttributes(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    var player: Player = Player(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_attribute_id")
    var playerStat: PlayerAttribute = PlayerAttribute(PlayerAttributeEnum.NOTHING),

    @Column(name = "value")
    var value: Float = 0f

) : BaseEntity<Long>()
