package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne


@Entity(name = "item_stats")
@AttributeOverride(name = "id", column = Column(name = "item_stats_id"))
data class ItemStats(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var item: Item = Item(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_stat_id")
    var itemStat: ItemStat = ItemStat(),

    @Column(name = "value")
    var value: Float = 0f,

    ) : BaseEntity<Long>()
