package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "class_initial_stats")
@AttributeOverride(name = "id", column = Column(name = "class_initial_stats_id"))
data class PlayerClassInitialStats(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_class_id")
    var playerClass: PlayerClass = PlayerClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat = PlayerStat(),

    @Column(name = "value")
    var value: Int = 0
)  : BaseEntity<Short>()
