package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.PlayerAttributeEnum
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "character_attribute")
@AttributeOverride(name = "id", column = Column(name = "character_attribute_id"))
data class PlayerAttribute(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: PlayerAttributeEnum = PlayerAttributeEnum.NOTHING

) : BaseEntity<Byte>() {

    init {
        this.id = this.code.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerAttribute")
    var formulas: MutableList<PlayerStatDependency> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerAttribute")
    var levelAttributes: MutableList<PlayerClassLevelAttributes> = mutableListOf()
}
