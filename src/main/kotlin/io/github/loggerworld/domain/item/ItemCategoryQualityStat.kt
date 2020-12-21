package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "item_category_quality_stat")
@AttributeOverride(name = "id", column = Column(name = "item_category_quality_stat_id"))
data class ItemCategoryQualityStat(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_quality_id")
    var itemCategoryQuality: ItemCategoryQuality = ItemCategoryQuality(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_stat_id")
    var itemStat: ItemStat = ItemStat(),

    @Column(name = "level")
    var level: Byte = 0,

    @Column(name = "min_value")
    var minValue: Float = 0f,

    @Column(name = "max_value")
    var maxValue: Float = 0f,

    ) : BaseEntity<Short>()
