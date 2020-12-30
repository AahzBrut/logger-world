package io.github.loggerworld.domain.loot

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.item.ItemQuality
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


@Entity(name = "loot_drop_quality")
@AttributeOverride(name = "id", column = Column(name = "loot_drop_quality_id"))
data class LootDropQuality(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loot_drop_category_id")
    var category: LootDropCategory = LootDropCategory(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_quality_id")
    var itemQuality: ItemQuality = ItemQuality(),

    @Column(name = "probability")
    var probability: Float = 0f,

    @Column(name = "quantity_min")
    var quantityMin: Float = 0f,

    @Column(name = "quantity_max")
    var quantityMax: Float = 0f,


    ) : BaseEntity<Short>()