package io.github.loggerworld.domain.monster

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne

@Entity(name = "monster_class_description")
@AttributeOverride(name = "id", column = Column(name = "monster_class_description_id"))
data class MonsterClassDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_class_id")
    var monsterClass: MonsterClass = MonsterClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Short>() {

}
