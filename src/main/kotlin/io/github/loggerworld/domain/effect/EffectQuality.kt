package io.github.loggerworld.domain.effect

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.item.ItemQuality
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "effect_quality")
@AttributeOverride(name = "id", column = Column(name = "effect_quality_id"))
data class EffectQuality(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effect_id")
    var effect: Effect = Effect(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_quality_id")
    var itemQuality: ItemQuality = ItemQuality(),

    ) : BaseEntity<Int>()