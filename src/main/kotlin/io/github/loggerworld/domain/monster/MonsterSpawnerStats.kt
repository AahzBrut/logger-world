package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.PlayerStat
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "monster_spawner_stats")
@AttributeOverride(name = "id", column = Column(name = "monster_spawner_stats_id"))
data class MonsterSpawnerStats(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_spawner_id")
    var monsterSpawner: MonsterSpawner = MonsterSpawner(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat = PlayerStat(),

    @Column(name = "value")
    var name: Int = 0

    ) : BaseEntity<Short>()