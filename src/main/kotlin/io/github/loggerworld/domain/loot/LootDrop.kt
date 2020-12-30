package io.github.loggerworld.domain.loot

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.monster.MonsterClass
import io.github.loggerworld.domain.monster.MonsterType
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "loot_drop")
@AttributeOverride(name = "id", column = Column(name = "loot_drop_id"))
data class LootDrop(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_class_id")
    var monsterClass: MonsterClass = MonsterClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_type_id")
    var monsterType: MonsterType = MonsterType(),

    @Column(name = "level")
    var level: Byte = 0,

    ) : BaseEntity<Short>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "lootDrop")
    var categories: List<LootDropCategory> = emptyList()
    }
