package io.github.loggerworld.domain.geography

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.monster.MonsterSpawner
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "location_monster_spawner")
@AttributeOverride(name = "id", column = Column(name = "location_monster_spawner_id"))
data class LocationMonsterSpawner(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    var location: Location = Location(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_spawner_id")
    var monsterSpawner: MonsterSpawner = MonsterSpawner(),

    @Column(name = "amount")
    var amount: Short = Short.MIN_VALUE,

    @Column(name = "min_respawn_time")
    var minRespawnTime: Double = 0.0,

    @Column(name = "max_respawn_time")
    var maxRespawnTime: Double = 0.0,

    ) : BaseEntity<Short>() {
}