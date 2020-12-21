package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


@Entity(name = "item_category_quality")
@AttributeOverride(name = "id", column = Column(name = "item_category_quality_id"))
data class ItemCategoryQuality(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    var itemCategory: ItemCategory = ItemCategory(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_quality_id")
    var itemQuality: ItemQuality = ItemQuality(),

    ) : BaseEntity<Short>()
