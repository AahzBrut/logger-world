package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.CalcTypes
import io.github.loggerworld.domain.main.CalcType
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "character_stat_dependency")
@AttributeOverride(name = "id", column = Column(name = "character_stat_dependency_id"))
data class PlayerStatDependency(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    var playerStat: PlayerStat = PlayerStat(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_attribute_id")
    var playerAttribute: PlayerAttribute = PlayerAttribute(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calc_type_id")
    var calcType: CalcType = CalcType(CalcTypes.ADD_MUL_COEFF),

    @Column(name = "coeff")
    var coeff: Float = 0f,

    ) : BaseEntity<Short>()
