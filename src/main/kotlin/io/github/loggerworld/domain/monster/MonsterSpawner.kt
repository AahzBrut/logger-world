package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import java.math.BigDecimal
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "monster_spawner")
@AttributeOverride(name = "id", column = Column(name = "monster_spawner_id"))
data class MonsterSpawner(

    @Column(name = "level")
    var level: Byte = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_class_id")
    var monsterClass: MonsterClass = MonsterClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_type_id")
    var monsterType: MonsterType = MonsterType(),

    @Column(name = "probability")
    var probability: BigDecimal = BigDecimal.ZERO

) : BaseEntity<Short>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "monsterSpawner")
    var stats: MutableList<MonsterSpawnerStats> = mutableListOf()
}
