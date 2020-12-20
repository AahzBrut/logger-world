package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.equipment.PlayerInventory
import java.time.OffsetDateTime
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "item")
@AttributeOverride(name = "id", column = Column(name = "item_id"))
data class Item(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    var itemCategory: ItemCategory = ItemCategory(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_quality_id")
    var itemQuality: ItemQuality = ItemQuality(),

    @Column(name = "quantity")
    var quantity: Long = 0,

    @Column(name = "created_at")
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    ) : BaseEntity<Long>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "item")
    var itemStats: MutableList<ItemStats> = mutableListOf()
}
