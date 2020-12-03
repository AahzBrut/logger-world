package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.PlayerStat
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "spawner_stats")
@AttributeOverride(name = "id", column = Column(name = "spawner_stats_id"))
data class MonsterSpawnerStats(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spawner_type_id")
    var spawnerType: MonsterSpawnerType = MonsterSpawnerType(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat = PlayerStat(),

    @Column(name = "value")
    var value: Double = 0.0

    ) : BaseEntity<Short>()