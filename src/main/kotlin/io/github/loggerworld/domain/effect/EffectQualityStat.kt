package io.github.loggerworld.domain.effect

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.PlayerStat
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "effect_quality_stat")
@AttributeOverride(name = "id", column = Column(name = "effect_quality_stat_id"))
data class EffectQualityStat(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effect_quality_id")
    var effect: EffectQuality = EffectQuality(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat,

    @Column(name = "min_value")
    var minValue: Float = 0f,

    @Column(name = "max_value")
    var maxValue: Float = 0f,

    ) : BaseEntity<Int>()