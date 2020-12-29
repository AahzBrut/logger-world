package io.github.loggerworld.domain.effect

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.CalcType
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "effect")
@AttributeOverride(name = "id", column = Column(name = "effect_id"))
data class Effect(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_effect_id")
    var parentEffect: Effect? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calc_type_id")
    var calcType: CalcType = CalcType(),

    @Column(name = "code")
    var code: String = "",

    @Column(name = "is_final")
    var isFinal: Boolean = false,

    ) : BaseEntity<Short>()
