package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.ChangeType
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "class_level_stats")
@AttributeOverride(name = "id", column = Column(name = "class_level_stats_id"))
data class PlayerClassLevelStats(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_type_id")
    var changeType: ChangeType = ChangeType(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_class_id")
    var playerClass: PlayerClass = PlayerClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat = PlayerStat(),

    @Column(name = "level")
    var level: Byte = 0,

    @Column(name = "value")
    var value: Double = 0.0
)  : BaseEntity<Short>()
