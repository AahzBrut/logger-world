package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.MonsterClasses
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "monster_class")
@AttributeOverride(name = "id", column = Column(name = "monster_class_id"))
data class MonsterClass(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: MonsterClasses = MonsterClasses.GREY_RAT

) : BaseEntity<Byte>() {

    init {
        this.id = code.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "monsterClass")
    var descriptions: MutableList<MonsterClassDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "monsterClass")
    var spawners: MutableList<MonsterSpawner> = mutableListOf()
}
