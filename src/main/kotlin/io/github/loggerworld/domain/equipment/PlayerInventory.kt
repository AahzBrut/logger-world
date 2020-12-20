package io.github.loggerworld.domain.equipment

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.Player
import io.github.loggerworld.domain.item.Item
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


@Entity(name = "character_inventory")
@AttributeOverride(name = "id", column = Column(name = "character_inventory_id"))
data class PlayerInventory(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    var player: Player = Player(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var item: Item = Item(),

    ) : BaseEntity<Long>()
