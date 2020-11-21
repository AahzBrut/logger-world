package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.PlayerClasses
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "character_class")
@AttributeOverride(name = "id", column = Column(name = "character_class_id"))
data class PlayerClass(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: PlayerClasses = PlayerClasses.DUMMY

) : BaseEntity<Byte>() {

    init {
        this.id = this.code.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerClass")
    var playerClassDescriptions: MutableList<PlayerClassDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerClass")
    var players: MutableList<Player> = mutableListOf()
}