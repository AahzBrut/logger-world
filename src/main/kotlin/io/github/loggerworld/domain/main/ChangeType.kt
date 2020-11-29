package io.github.loggerworld.domain.main

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.PlayerClassLevelStats
import io.github.loggerworld.domain.enums.ChangeTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "change_type")
@AttributeOverride(name = "id", column = Column(name = "change_type_id"))
data class ChangeType(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: ChangeTypes = ChangeTypes.SET,

    ) : BaseEntity<Byte>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "changeType")
    var playerClassLevelStats: MutableList<PlayerClassLevelStats> = mutableListOf()

}