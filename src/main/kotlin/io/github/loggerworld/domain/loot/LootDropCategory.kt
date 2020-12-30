package io.github.loggerworld.domain.loot

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.item.ItemCategory
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "loot_drop_category")
@AttributeOverride(name = "id", column = Column(name = "loot_drop_category_id"))
data class LootDropCategory(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loot_drop_id")
    var lootDrop: LootDrop = LootDrop(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    var itemCategory: ItemCategory = ItemCategory(),

    @Column(name = "probability")
    var probability: Float = 0f,

) : BaseEntity<Short>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "category")
    var qualities: List<LootDropQuality> = emptyList()
}
