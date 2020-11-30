package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.MonsterTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "monster_type")
@AttributeOverride(name = "id", column = Column(name = "monster_type_id"))
data class MonsterType(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: MonsterTypes = MonsterTypes.NORMAL

) : BaseEntity<Byte>(){

    init {
        this.id = code.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "monsterType")
    var descriptions: MutableList<MonsterTypeDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "monsterType")
    var spawners: MutableList<MonsterSpawner> = mutableListOf()
}
